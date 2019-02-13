package com.easipass.epia.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * Created by lql on 2017/8/3.
 */
public class ObjectFormater {
    /**
     * 获取一个对象的拷贝,需要传入对象实现Serializable接口
     * @param object
     * @return
     * @throws Exception
     */
    public static Object clone(Object object)throws Exception
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(object);
        ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
        ObjectInputStream ois = new ObjectInputStream(bais);
        return ois.readObject();
    }

    /**
     * 把结果对象转成字节流
     * @param object
     * @return
     * @throws Exception
     */
    public static byte[] toByteArray(Object object) throws Exception {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream oos = null;
        oos = new ObjectOutputStream(bos);
        oos.writeObject(object);
        oos.flush();
        oos.close();
        bos.close();
        return bos.toByteArray();
    }
}
