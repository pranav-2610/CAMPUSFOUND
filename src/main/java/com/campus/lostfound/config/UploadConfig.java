package com.campus.lostfound.config;
import org.springframework.context.annotation.Configuration;import org.springframework.web.servlet.config.annotation.*;import java.nio.file.*;
@Configuration public class UploadConfig implements WebMvcConfigurer { public void addResourceHandlers(ResourceHandlerRegistry r){String dir=Path.of(System.getProperty("campus.upload.dir","uploads")).toAbsolutePath().normalize().toUri().toString();r.addResourceHandler("/media/**").addResourceLocations(dir);} }
