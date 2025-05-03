package com.example.notificationservice.service;

import lombok.RequiredArgsConstructor;
import org.example.common.dto.Notification.NotificationType;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class NotificationTemplateService {
    private final TemplateEngine templateEngine;

    public String renderTitle(NotificationType type, Map<String, Object> data) {
        return renderTemplate("notifications/" + type.name() + "_title", data);
    }

    public String renderContent(NotificationType type, Map<String, Object> data) {
        return renderTemplate("notifications/" + type.name() + "_content", data);
    }

    private String renderTemplate(String templatePath, Map<String,Object> metaData){
        Context context = new Context();
        metaData.forEach(context ::setVariable);
        return templateEngine.process(templatePath, context);
    }
}
