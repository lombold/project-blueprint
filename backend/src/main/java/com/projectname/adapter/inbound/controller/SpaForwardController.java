package com.projectname.adapter.inbound.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Forwards Angular SPA routes to {@code /ui/index.html}.
 *
 * <p>Spring Boot's default static-resource handler serves actual files (JS, CSS, images)
 * from {@code classpath:/static/ui/} first. This controller only kicks in for paths
 * that do <em>not</em> map to a real file — i.e., Angular client-side routes.</p>
 */
@Controller
public class SpaForwardController {

    /**
     * Matches /ui and any sub-path whose last segment has no file extension.
     * Examples that forward: /ui, /ui/, /ui/users, /ui/users/42/edit
     * Examples that DON'T match (served as static files): /ui/main-abc.js, /ui/styles.css
     */
    @GetMapping("/ui")
    public String forwardRoot() {
        return "forward:/ui/index.html";
    }

    @GetMapping("/ui/{path:^(?!.*\\.).*$}")
    public String forwardSingleSegment() {
        return "forward:/ui/index.html";
    }

    @GetMapping("/ui/{path:^(?!.*\\.).*$}/**")
    public String forwardDeepRoute() {
        return "forward:/ui/index.html";
    }
}