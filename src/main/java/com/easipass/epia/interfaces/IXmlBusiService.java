package com.easipass.epia.interfaces;

import com.easipass.epia.util.ResponseResult;

import java.util.List;
import java.util.Map;

/**
 * Created by lql on 2018/12/26 11:19
 **/
public interface IXmlBusiService {
    ResponseResult exec(String jsonparam, Map<String, List<Map<String, byte[]>>> fileMap);
}
