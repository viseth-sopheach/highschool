package com.seth.backend.audit;

/** String constants so call sites and the AUDIT_READ dashboard agree on exact action names. */
public final class AuditActions {
   private AuditActions() {}

   public static final String LOGIN_SUCCESS = "LOGIN_SUCCESS";
   public static final String LOGIN_FAILURE = "LOGIN_FAILURE";
   public static final String ACCOUNT_LOCKED = "ACCOUNT_LOCKED";
   public static final String LOGOUT = "LOGOUT";
   public static final String TOKEN_REFRESH = "TOKEN_REFRESH";

   public static final String USER_CREATED = "USER_CREATED";
   public static final String USER_STATUS_CHANGED = "USER_STATUS_CHANGED";
   public static final String USER_ROLES_CHANGED = "USER_ROLES_CHANGED";

   public static final String STUDENT_CREATED = "STUDENT_CREATED";
   public static final String STUDENT_UPDATED = "STUDENT_UPDATED";
   public static final String STUDENT_DELETED = "STUDENT_DELETED";

   public static final String TEACHER_CREATED = "TEACHER_CREATED";
   public static final String TEACHER_UPDATED = "TEACHER_UPDATED";
   public static final String TEACHER_DELETED = "TEACHER_DELETED";

   public static final String ENROLLMENT_CREATED = "ENROLLMENT_CREATED";
   public static final String ENROLLMENT_STATUS_CHANGED = "ENROLLMENT_STATUS_CHANGED";

   public static final String SCORE_UPSERTED = "SCORE_UPSERTED";
   public static final String ATTENDANCE_MARKED = "ATTENDANCE_MARKED";
}