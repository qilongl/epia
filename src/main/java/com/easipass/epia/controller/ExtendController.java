package com.easipass.epia.controller;

import com.easipass.epia.service.XmlBusiService;
import com.easipass.epia.util.*;
import io.swagger.annotations.ApiOperation;
import net.sf.json.JSONArray;
import org.apache.commons.collections.map.HashedMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import sun.misc.BASE64Encoder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by lql on 2018/12/26 15:35
 * 废弃 2019/07/12
 **/
@RestController
@Scope("prototype")//防止单例出现竞争访问
public class ExtendController {
    private static Logger logger = LoggerFactory.getLogger(ExtendController.class);

    @Autowired
    XmlBusiService xmlBusiService;

}
