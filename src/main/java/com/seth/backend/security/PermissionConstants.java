package com.seth.backend.security;

public final class PermissionConstants {
   private PermissionConstants() {}

   public static final String STUDENT_READ = "hasAuthority('PERM_STUDENT_READ')";
   public static final String STUDENT_READ_OWN = "hasAuthority('PERM_STUDENT_READ_OWN')";
   public static final String STUDENT_READ_ANY_OR_OWN = "hasAnyAuthority('PERM_STUDENT_READ','PERM_STUDENT_READ_OWN')";
   public static final String STUDENT_WRITE = "hasAuthority('PERM_STUDENT_WRITE')";

   public static final String TEACHER_READ = "hasAuthority('PERM_TEACHER_READ')";
   public static final String TEACHER_WRITE = "hasAuthority('PERM_TEACHER_WRITE')";

   public static final String CLASS_READ = "hasAuthority('PERM_CLASS_READ')";
   public static final String CLASS_WRITE = "hasAuthority('PERM_CLASS_WRITE')";

   public static final String ENROLLMENT_READ = "hasAuthority('PERM_ENROLLMENT_READ')";
   public static final String ENROLLMENT_WRITE = "hasAuthority('PERM_ENROLLMENT_WRITE')";

   public static final String ATTENDANCE_READ = "hasAuthority('PERM_ATTENDANCE_READ')";
   public static final String ATTENDANCE_READ_OWN = "hasAuthority('PERM_ATTENDANCE_READ_OWN')";
   public static final String ATTENDANCE_READ_ANY_OR_OWN = "hasAnyAuthority('PERM_ATTENDANCE_READ','PERM_ATTENDANCE_READ_OWN')";
   public static final String ATTENDANCE_WRITE = "hasAuthority('PERM_ATTENDANCE_WRITE')";

   public static final String ASSESSMENT_READ = "hasAuthority('PERM_ASSESSMENT_READ')";
   public static final String ASSESSMENT_WRITE = "hasAuthority('PERM_ASSESSMENT_WRITE')";

   public static final String SCORE_READ = "hasAuthority('PERM_SCORE_READ')";
   public static final String SCORE_READ_OWN = "hasAuthority('PERM_SCORE_READ_OWN')";
   public static final String SCORE_READ_ANY_OR_OWN = "hasAnyAuthority('PERM_SCORE_READ','PERM_SCORE_READ_OWN')";
   public static final String SCORE_WRITE = "hasAuthority('PERM_SCORE_WRITE')";

   public static final String USER_READ = "hasAuthority('PERM_USER_READ')";
   public static final String USER_MANAGE = "hasAuthority('PERM_USER_MANAGE')";
   public static final String ROLE_MANAGE = "hasAuthority('PERM_ROLE_MANAGE')";
   public static final String AUDIT_READ = "hasAuthority('PERM_AUDIT_READ')";
}