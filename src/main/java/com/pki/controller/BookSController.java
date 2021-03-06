package com.pki.controller;

import com.pki.entity.Cabook;
import com.pki.entity.User;
import com.pki.enums.CAState;
import com.pki.service.Impl.CABookService;
import com.pki.service.Impl.UserService;
import com.pki.utils.BookUtils;
import com.pki.utils.PropertiesUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author by twjitm on 2018/12/21/11:05
 */
@Controller
@RequestMapping("books")
public class BookSController extends BaseController {

    private static Logger logger = LoggerFactory.getLogger(BookSController.class);
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    @Autowired
    private CABookService cABookService;
    @Autowired
    private UserService userService;


    //-------该写证书的啦
    //普通用户申请证书
    @RequestMapping("apply")
    public String apply(HttpServletRequest request, Cabook cabook) {
        User user = getconcurrentUser(request);
        if (null == user) {
            logger.info("--------------null---------");
            return "login";
        } else {
            Date d = new Date();
            SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd");
            String url = f.format(d);
            String bookPath = PropertiesUtils.getBookPath();
            String caUrl = bookPath + user.getUName() + url + ".keystore";
            cabook.setCaUrl(caUrl);
            cABookService.Save(cabook);
            BookUtils.genkey(cabook);
            return "success";
        }
    }

    //普通用户默认查询
    @RequestMapping("selectdef")
    public String selectdef(HttpServletRequest request) {
        User user = getconcurrentUser(request);
        List<Cabook> list = cABookService.getBooKById(user.getUId());
        request.setAttribute("list", list);
        return "select";
    }

    private String caselecttype;

    //普通用户按状态查询
    @RequestMapping("select")
    public String select(HttpServletRequest request) {
        User user = getconcurrentUser(request);
        List<Cabook> list = cABookService.getBookByUId(user.getUId(), caselecttype);
        request.setAttribute("list", list);
        return "select";
    }


    //管理员条件查询证书
    @RequestMapping("adminquery")
    public List<Cabook> adminquery(HttpServletRequest request, String adcaState) {
        User user = getconcurrentUser(request);
        List<Cabook> list = new ArrayList<>();
        if (null == user) {
        } else {
            list = cABookService.getBookByStart(adcaState);
        }
        return list;
    }

    //管理员默认查询所有证书
    @RequestMapping("adminQueryNoParams")
    public String adminQueryNoParams(HttpServletRequest request) {
        User user = getconcurrentUser(request);
        if (null == user) {
            return "login";
        } else {
            List<Cabook> list = cABookService.getBookByStart();
            request.setAttribute("list", list);
            return "adminselect";
        }
    }

    //管理员查看证书详细信息
    @RequestMapping("adminselectCaInfor")
    public String adminselectCaInfor(HttpServletRequest request, Integer caBookId) {
        User user = getconcurrentUser(request);
        if (null == user) {
            return "login";
        } else {
            Cabook cabook = cABookService.getCaBookById(caBookId);
            request.setAttribute("cabook", cabook);
            return "cainfo";
        }
    }

    private Integer caBookId;

    //管理员签发证书
    @RequestMapping("adminsetCAbook")
    public String adminsetCAbook(HttpServletRequest request, Integer caBookId) {
        User user = getconcurrentUser(request);
        if (null == user) {
            return "login";
        } else {

            Cabook cabook = cABookService.getCaBookById(caBookId);
            System.out.println("------------>>" + cabook.getCaCn());
            BookUtils.export(cabook, user);
            cABookService.updata(cabook);
            return "getbookcar";
        }
    }

    //管理员删除证书
    @RequestMapping("deleteca")
    public String deleteca() {
        Cabook cabook = cABookService.getCaBookById(caBookId);
        java.io.File file = new java.io.File(cabook.getCaUrl());
        cABookService.delete(cabook);
        if (file.exists())
            file.delete();
        return "getbookcar";

    }


    //--------------证书下载
    @RequestMapping("getDownloadFile")
    public String getDownloadFile(HttpServletRequest request, HttpServletResponse response, Integer downCaBookId) {
        Cabook car = cABookService.getCaBookById(downCaBookId);
        String inputPath = car.getCaUrl();
        if (inputPath != null && !"".equals(inputPath)) {
            response.setHeader("content-disposition", "attachment;filename=certificate" + car.getCaC() + inputPath.substring(inputPath.indexOf("."), inputPath.length()));
            byte[] buf = new byte[1000];
            FileInputStream fos = null;
            try {
                String file = car.getCaUrl();
                fos = new FileInputStream(file);
                ServletOutputStream out = response.getOutputStream();
                while (fos.read(buf) != -1) {
                    out.write(buf);
                }
                response.flushBuffer();
                out.close();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    fos.close();
                } catch (Exception f) {
                }
            }
        } else {
            try {
                String message = "还没有上传文件";
                response.setContentType("text/html;charset=UTF-8");
                PrintWriter out = response.getWriter();
                out.write(message);
                out.flush();
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
