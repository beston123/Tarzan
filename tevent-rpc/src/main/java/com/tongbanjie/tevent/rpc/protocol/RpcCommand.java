package com.tongbanjie.tevent.rpc.protocol;

import com.tongbanjie.tevent.common.TEventVersion;
import com.tongbanjie.tevent.rpc.exception.RpcCommandException;
import com.tongbanjie.tevent.rpc.util.ClassUtils;
import com.tongbanjie.tevent.rpc.util.RpcSerializeUtils;
import com.tongbanjie.tevent.rpc.protocol.header.CustomHeader;
import org.apache.commons.collections4.MapUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Rpc命令 <p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 16/9/27
 */
public class RpcCommand {

    //Request
    public static final byte REQUEST_COMMAND = 0x00;
    //Response
    public static final byte RESPONSE_COMMAND = 0x01;

    private static AtomicInteger requestId = new AtomicInteger(0);

    /**************** header ****************/
    /**
     * 命令编码
     *
     * @see RequestCode
     * @see ResponseCode
     */
    private int cmdCode;

    /**
     * 协议类型
     *
     * @see SerializeType
     */
    private SerializeType serializeType = SerializeType.JSON;

    /**
     * 命令类型
     *
     * RpcCommand.REQUEST_COMMAND
     * RpcCommand.RESPONSE_COMMAND
     */
    private byte cmdType = REQUEST_COMMAND;

    /**
     * 程序版本
     */
    private int version = TEventVersion.CURRENT.getValue();

    //自定义字段 customHeader中的字段
    private Map<String, String> customFields;

    /**
     * cmd唯一Id
     * 确保Request和Response对应
     * 
     */
    private int opaque = requestId.getAndIncrement();

    private boolean oneWayRpc = false;

    private String remark;

    /**************** body, customHeader ****************/

    private transient CustomHeader customHeader;

    private transient byte[] body;

    /*************** 内部使用 ***************/
    private static final Map<Class<? extends CustomHeader>, Field[]> clazzFieldsCache =
            new HashMap<Class<? extends CustomHeader>, Field[]>();

    private static final Map<Class, String> canonicalNameCache = new HashMap<Class, String>();

    protected RpcCommand() {}

    public static RpcCommand decode(final byte[] array) {
        ByteBuffer byteBuffer = ByteBuffer.wrap(array);
        return decode(byteBuffer);
    }

    public static RpcCommand decode(final ByteBuffer byteBuffer) {
        //总长度
        int length = byteBuffer.limit();
        //协议类型 1byte
        byte protocolType = byteBuffer.get();
        //Header长度 4type
        int headerLength = byteBuffer.getInt();
        //Header内容
        byte[] headerData = new byte[headerLength];
        byteBuffer.get(headerData);

        RpcCommand cmd = headerDecode(headerData, getProtocolType(protocolType));
        //body长度
        int bodyLength = length - 5 - headerLength;
        byte[] bodyData = null;
        if (bodyLength > 0) {
            bodyData = new byte[bodyLength];
            byteBuffer.get(bodyData);
        }
        cmd.body = bodyData;

        return cmd;
    }

    public static RpcCommand headerDecode(byte[] headerData, SerializeType type) {
        return RpcSerializeUtils.deserialize(headerData, RpcCommand.class, type);
    }

    public static int createNewRequestId() {
        return requestId.incrementAndGet();
    }

    public CustomHeader decodeCustomHeader(Class<? extends CustomHeader> classHeader) throws RpcCommandException {
        CustomHeader objectHeader;
        try {
            objectHeader = classHeader.newInstance();
        } catch (InstantiationException e) {
            return null;
        } catch (IllegalAccessException e) {
            return null;
        }
        if ( MapUtils.isNotEmpty(this.customFields) ) {
            Field[] fields = getClazzFields(classHeader);
            for (Field field : fields) {
                String fieldName = field.getName();
                Class clazz = field.getType();
                if (Modifier.isStatic(field.getModifiers()) || fieldName.startsWith("this")) {
                    continue;
                }
                String value = this.customFields.get(fieldName);
                if(value == null){
                    continue;
                }
                field.setAccessible(true);
                Object valueParsed;
                if(clazz.isEnum()){
                    valueParsed = Enum.valueOf(clazz, value);
                }else{
                    String type = getCanonicalName(clazz);
                    valueParsed = ClassUtils.parseSimpleValue(type, value);
                }
                if(valueParsed == null){
                    throw new RpcCommandException("Encode the header failed, the custom field <" + fieldName
                            + "> type <" + getCanonicalName(clazz) + "> is not supported.");
                }
                try {
                    field.set(objectHeader, valueParsed);
                } catch (IllegalAccessException e) {
                    throw new RpcCommandException("Encode the header failed, set the value of field < "+ fieldName
                            + "> error.", e);
                }
            }

            objectHeader.checkFields();
        }

        return objectHeader;
    }

    private Field[] getClazzFields(Class<? extends CustomHeader> classHeader) {
        Field[] field = clazzFieldsCache.get(classHeader);

        if (field == null) {
            field = classHeader.getDeclaredFields();
            synchronized (clazzFieldsCache) {
                clazzFieldsCache.put(classHeader, field);
            }
        }
        return field;
    }

    private String getCanonicalName(Class clazz) {
        String name = canonicalNameCache.get(clazz);

        if (name == null) {
            name = clazz.getCanonicalName();
            synchronized (canonicalNameCache) {
                canonicalNameCache.put(clazz, name);
            }
        }
        return name;
    }

    public ByteBuffer encode() throws RpcCommandException {
        /******* 计算数据长度 *******/
        // 1> protocol type size
        int length = Protocol.PROTOCOL_TYPE_SIZE;

        // 2> header length size
        length += Protocol.HEADER_LENGTH_SIZE;

        // 3> header data length
        byte[] headerData = this.headerEncode();
        length += headerData.length;

        // 4> body data length
        if (this.body != null) {
            length += body.length;
        }

        /******* 写入ByteBuffer *******/
        //分配空间
        ByteBuffer result = ByteBuffer.allocate(Protocol.TOTAL_LENGTH_SIZE + length);

        // 0、length
        result.putInt(length);

        // 1、protocol type
        result.put(markProtocolType(serializeType));

        // 2、header length
        result.putInt(headerData.length);

        // 3、header data
        result.put(headerData);

        // 4、body data;
        if (this.body != null) {
            result.put(this.body);
        }

        result.flip();

        return result;
    }

    public ByteBuffer encodeHeader() throws RpcCommandException {
        return encodeHeader(this.body != null ? this.body.length : 0);
    }

    public ByteBuffer encodeHeader(final int bodyLength) throws RpcCommandException {
        /******* 计算数据长度 *******/
        // 1> protocol type size
        int length = Protocol.PROTOCOL_TYPE_SIZE;

        // 2> header length size
        length += Protocol.HEADER_LENGTH_SIZE;

        // 3> header data length
        byte[] headerData;
        headerData = this.headerEncode();
        length += headerData.length;

        // 4> body data length
        length += bodyLength;

        /******* 写入ByteBuffer *******/
        //分配空间，不存body
        ByteBuffer result = ByteBuffer.allocate(Protocol.TOTAL_LENGTH_SIZE + length - bodyLength);

        // 0、length
        result.putInt(length);

        // 1、protocol type
        result.put(markProtocolType(serializeType));

        // 2、header length
        result.putInt(headerData.length);

        // 3、header data
        result.put(headerData);

        result.flip();

        return result;
    }

    private byte[] headerEncode() throws RpcCommandException {
        this.customHeaderToMap();
        return RpcSerializeUtils.serialize(this, serializeType);
    }

    /**
     * 设置协议类型
     * @param type
     * @return
     */
    public static byte markProtocolType(SerializeType type) {
        return type.getCode();
    }

    /**
     * 获取协议类型
     * @param source
     * @return
     */
    public static SerializeType getProtocolType(byte source) {
        return SerializeType.valueOf(source);
    }

    private void customHeaderToMap() throws RpcCommandException {
        if (this.customHeader != null) {
            Field[] fields = getClazzFields(customHeader.getClass());
            if (null == this.customFields) {
                this.customFields = new HashMap<String, String>(8);
            }
            for (Field field : fields) {
                String fieldName = field.getName();
                Class clazz = field.getType();
                if (Modifier.isStatic(field.getModifiers()) || fieldName.startsWith("this")) {
                    continue;
                }
                Object value;
                try {
                    field.setAccessible(true);
                    value = field.get(this.customHeader);
                } catch (Exception e) {
                    throw new RpcCommandException("Encode the header failed, get the value of field <" + fieldName
                            + "> error.", e);
                }
                if (value == null) {
                    continue;
                }
                if(clazz.isEnum()) {
                    this.customFields.put(fieldName, value.toString());
                }else{
                    String type = getCanonicalName(clazz);
                    String strValue = ClassUtils.simpleValueToString(type, value);
                    if(strValue == null){
                        throw new RpcCommandException("Encode the header failed, the field <" + fieldName
                                + "> type <" + getCanonicalName(clazz) + "> is not supported.");
                    }
                    this.customFields.put(fieldName, strValue);
                }
            }
        }
    }

    public SerializeType getSerializeType() {
        return serializeType;
    }

    public void setSerializeType(SerializeType serializeType) {
        this.serializeType = serializeType;
    }

    public int getCmdCode() {
        return cmdCode;
    }

    public void setCmdCode(int cmdCode) {
        this.cmdCode = cmdCode;
    }

    public byte getCmdType() {
        return cmdType;
    }

    public void setCmdType(byte cmdType) {
        this.cmdType = cmdType;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public Map<String, String> getCustomFields() {
        return customFields;
    }

    public void setCustomFields(Map<String, String> customFields) {
        this.customFields = customFields;
    }

    public int getOpaque() {
        return opaque;
    }

    public void setOpaque(int opaque) {
        this.opaque = opaque;
    }

    public boolean isOneWayRpc() {
        return oneWayRpc;
    }

    public void setOneWayRpc(boolean oneWayRpc) {
        this.oneWayRpc = oneWayRpc;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public byte[] getBody() {
        return body;
    }

    public <T> T getBody(Class<T> clazz){
        if(this.body == null){
            return null;
        }
        return RpcSerializeUtils.deserialize(this.body, clazz, this.serializeType);
    }

    public void setBody(byte[] body) {
        this.body = body;
    }

    public void setBody(Object obj){
        this.body = RpcSerializeUtils.serialize(obj, this.serializeType);
    }

    public CustomHeader getCustomHeader() {
        return customHeader;
    }

    public void setCustomHeader(CustomHeader customHeader) {
        this.customHeader = customHeader;
    }

    @Override
    public String toString() {
        return "RpcCommand{" +
                "cmdCode=" + cmdCode +
                ", serializeType=" + serializeType +
                ", cmdType=" + cmdType +
                ", version=" + version +
                ", opaque=" + opaque +
                ", oneWayRpc=" + oneWayRpc +
                ", remark='" + remark + '\'' +
                ", customHeader=" + customHeader +
                ", body=" + Arrays.toString(body) +
                '}';
    }
}