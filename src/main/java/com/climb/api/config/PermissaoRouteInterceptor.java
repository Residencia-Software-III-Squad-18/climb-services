package com.climb.api.config;

import com.climb.api.model.PermissaoCodigo;
import org.springframework.web.method.HandlerMethod;
import com.climb.api.config.Permissao;
import com.climb.api.service.RbacService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.IOException;

@Component
public class PermissaoRouteInterceptor implements HandlerInterceptor {

    private final RbacService rbacService;

    public PermissaoRouteInterceptor(RbacService rbacService) {
        this.rbacService = rbacService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws IOException {

        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        Long usuarioId = getUsuarioIdAutenticado();
        if (usuarioId == null) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
            return false;
        }

        PermissaoCodigo permissaoNecessaria = resolverPermissao(request, handler);
        if (permissaoNecessaria == null) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Forbidden: missing route permission");
            return false;
        }

        if (!rbacService.temPermissao(usuarioId, permissaoNecessaria)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Forbidden");
            return false;
        }

        return true;
    }

    private Long getUsuarioIdAutenticado() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || authentication.getDetails() == null) {
            return null;
        }

        if (authentication.getDetails() instanceof Long) {
            return (Long) authentication.getDetails();
        }

        return null;
    }

    private PermissaoCodigo resolverPermissao(HttpServletRequest request, Object handler) {
        // Primeiro, tenta obter a permissão diretamente da anotação na rota (método ou classe)
        if (handler instanceof HandlerMethod) {
            HandlerMethod hm = (HandlerMethod) handler;
            Permissao metodoPerm = hm.getMethodAnnotation(Permissao.class);
            if (metodoPerm != null) {
                return metodoPerm.value();
            }

            Permissao classePerm = hm.getBeanType().getAnnotation(Permissao.class);
            if (classePerm != null) {
                return classePerm.value();
            }
        }

        // Somente resolução por anotação em método / classe
        return null;
    }
}