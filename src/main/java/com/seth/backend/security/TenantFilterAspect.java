package com.seth.backend.security;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.hibernate.Session;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Order(1)
public class TenantFilterAspect {

   @PersistenceContext
   private EntityManager entityManager;

   @Before("execution(* com.seth.backend..*.service..*(..))")
   public void enableTenantFilter() {
      Long schoolId = SecurityUtils.currentSchoolIdOrNull();
      if (schoolId == null) {
         return;
      }
      entityManager.unwrap(Session.class)
              .enableFilter("schoolFilter")
              .setParameter("schoolId", schoolId);
   }
}