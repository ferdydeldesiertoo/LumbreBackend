package com.lumbre.security.resolver

import com.lumbre.security.CustomUserDetails
import com.lumbre.security.annotation.CurrentUser
import org.springframework.core.MethodParameter
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.support.WebDataBinderFactory
import org.springframework.web.context.request.NativeWebRequest
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.method.support.ModelAndViewContainer

class CurrentUserArgumentResolver : HandlerMethodArgumentResolver {
    override fun supportsParameter(parameter: MethodParameter): Boolean {
        return parameter.hasParameterAnnotation(CurrentUser::class.java) && parameter.parameterType == CustomUserDetails::class.java
    }

    override fun resolveArgument(
        parameter: MethodParameter,
        mavContainer: ModelAndViewContainer?,
        webRequest: NativeWebRequest,
        binderFactory: WebDataBinderFactory?
    ): CustomUserDetails {
        val authentication = SecurityContextHolder.getContext().authentication

        return authentication?.principal as? CustomUserDetails ?: throw IllegalStateException("No authenticated user found")
    }
}