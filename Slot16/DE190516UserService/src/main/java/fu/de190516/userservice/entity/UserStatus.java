package fu.de190516.userservice.entity;

/**
 * Trang thai cua user. Luu vao DB duoi dang chuoi (nvarchar) qua @Enumerated(EnumType.STRING).
 */
public enum UserStatus {
    ACTIVE,
    SUSPENDED,
    DEACTIVATED
}
