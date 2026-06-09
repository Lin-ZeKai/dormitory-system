package com.dormitory.servlet;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * 显式提供 favicon，避免静态资源或过滤器导致 404
 */
@WebServlet(name = "FaviconServlet", urlPatterns = {"/favicon.ico", "/favicon.svg"})
public class FaviconServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String uri = req.getRequestURI();
        String path = uri.substring(req.getContextPath().length());
        String resource = "/favicon.ico".equals(path) ? "/favicon.ico" : "/favicon.svg";
        String contentType = resource.endsWith(".svg") ? "image/svg+xml" : "image/x-icon";

        InputStream in = getServletContext().getResourceAsStream(resource);
        if (in == null) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        resp.setContentType(contentType);
        resp.setHeader("Cache-Control", "public, max-age=604800");
        OutputStream out = resp.getOutputStream();
        byte[] buf = new byte[4096];
        int len;
        try {
            while ((len = in.read(buf)) != -1) {
                out.write(buf, 0, len);
            }
        } finally {
            in.close();
        }
    }
}
