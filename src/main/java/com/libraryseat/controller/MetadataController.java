package com.libraryseat.controller;

import com.libraryseat.pojo.LibraryMetadata;
import com.libraryseat.pojo.User;
import com.libraryseat.services.MetadataService;
import com.libraryseat.utils.JsonUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@Controller
public class MetadataController {
    @Autowired
    private MetadataService metadataService;

    @RequestMapping(value = "/librarymetadata.do",method = {RequestMethod.POST,RequestMethod.GET})
    @ResponseBody
    public void getLibraryMetadata(@RequestParam(defaultValue = "true") Boolean hideloc, HttpServletResponse resp, HttpSession session)
            throws IOException {
        User u = (User) session.getAttribute("user");
        if (u == null){
            resp.sendError(403,"校验失败");
            return;
        }
        resp.setContentType("application/json;charset=utf-8");
        LibraryMetadata metadata = metadataService.getMetadata();
        if (hideloc) {
            metadata = (LibraryMetadata) metadata.clone();
            metadata.setProvince("<hidden>");
            metadata.setCity("<hidden>");
        }
        JsonUtil.writeMetadata(metadata,resp.getOutputStream());
    }
}
