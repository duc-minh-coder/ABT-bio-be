package com.NMCNPM.ABT_bio.exception;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public enum ErrorCode {
    // 1000 - 1099: User
    USER_EXISTED(1000, "user existed", HttpStatus.BAD_REQUEST),
    USER_NOT_EXISTED(1001, "user not existed", HttpStatus.NOT_FOUND),
    SELLER_NOT_EXISTED(1001, "seller not existed", HttpStatus.NOT_FOUND),
    USERNAME_INVALID(1002, "username is invalid", HttpStatus.BAD_REQUEST),
    PASSWORD_INVALID(1003, "password is invalid", HttpStatus.BAD_REQUEST),
    NAME_SHORT(1004, "name is too short", HttpStatus.BAD_REQUEST),
    WRONG_PASSWORD(1005, "wrong password", HttpStatus.BAD_REQUEST),
    PASSWORD_CHANGE_TOO_SOON(1006, "password change too soon", HttpStatus.BAD_REQUEST),
    BANNED_USER_NOT_FOUND(1007, "can't find banned user", HttpStatus.NOT_FOUND),
    EMAIL_NOT_FOUND(1008, "email not found", HttpStatus.NOT_FOUND),
    TEMP_PASSWORD_INVALID(1009, "temp password invalid", HttpStatus.BAD_REQUEST),
    TEMP_PASSWORD_EXPIRED(1010, "temp password expired", HttpStatus.BAD_REQUEST),
    SELLER_ALREADY_BANNED(1011, "seller already banned", HttpStatus.BAD_REQUEST),
    SELLER_NOT_ALLOWED_TO_BUY(1012, "seller not allowed to buy", HttpStatus.FORBIDDEN),
    SELLER_NOT_BANNED(1013, "seller not banned", HttpStatus.BAD_REQUEST),
    SELLER_NOT_APPROVED(1014, "seller not approved", HttpStatus.FORBIDDEN),
    CANNOT_DELETE_ADMIN(1015, "cannot delete admin user", HttpStatus.FORBIDDEN),
    USER_NOT_LINKED(1016, "Not linked", HttpStatus.BAD_REQUEST),
    TELEGRAM_CHAT_ID_EXISTED(1017, "Tài khoản telegram này đã liên kết với tài khoản khác", HttpStatus.BAD_REQUEST),

    // 1100 - 1199: Profile
    PROFILE_NOT_EXISTED(1100, "profile not existed", HttpStatus.NOT_FOUND),
    NAME_CHANGE_TOO_SOON(1101, "name change too soon", HttpStatus.BAD_REQUEST),
    NAME_INVALID(1102, "name is limited to 3-20 letters only", HttpStatus.BAD_REQUEST),
    PROFILE_NAME_INVALID(1103, "Họ và tên phải có ít nhất 2 ký tự.", HttpStatus.UNPROCESSABLE_ENTITY),
    PROFILE_BIO_TOO_LONG(1104, "Tiểu sử không được vượt quá 500 ký tự.", HttpStatus.UNPROCESSABLE_ENTITY),
    EMAIL_ALREADY_EXISTS(1105, "Email đã được sử dụng.", HttpStatus.UNPROCESSABLE_ENTITY),
    ACCOUNT_LOCKED(1106, "Tài khoản của bạn đã bị khóa. Không thể cập nhật thông tin.", HttpStatus.FORBIDDEN),
    PHONE_INVALID(1107, "Số điện thoại không hợp lệ.", HttpStatus.UNPROCESSABLE_ENTITY),
    DATE_OF_BIRTH_INVALID(1108, "Ngày sinh không hợp lệ.", HttpStatus.UNPROCESSABLE_ENTITY),
    FILE_UPLOAD_FAILED(1109, "Không thể tải lên file. Vui lòng thử lại.", HttpStatus.INTERNAL_SERVER_ERROR),

    // 1200 - 1299: profile role
    VOLUNTEER_PROFILE_NOT_FOUND(1200, "Volunteer profile not found", HttpStatus.NOT_FOUND),
    ORGANIZER_PROFILE_NOT_FOUND(1201, "Organizer profile not found", HttpStatus.NOT_FOUND),
    PROFILE_UPDATE_FAILED(1202, "failed to update profile", HttpStatus.INTERNAL_SERVER_ERROR),
    INVALID_PROFILE_DATA(1203, "invalid profile data provided", HttpStatus.BAD_REQUEST),
    PROFILE_ACCESS_DENIED(1204, "access denied to this profile", HttpStatus.FORBIDDEN),

    // 1300 - 1399: search related errors
    INVALID_SEARCH_CRITERIA(1300, "Invalid search criteria", HttpStatus.BAD_REQUEST),
    SEARCH_LIMIT_EXCEEDED(1301, "Search limit exceeded", HttpStatus.TOO_MANY_REQUESTS),
    INVALID_REQUEST(1302, "INVALID_REQUEST", HttpStatus.BAD_REQUEST),
    // 2000 - 2100: blog
    POST_NOT_FOUND(2000, "post not found", HttpStatus.NOT_FOUND),

    // 3000 - 3100: payment
    ORDER_NOT_FOUND(3000, "order not found", HttpStatus.NOT_FOUND),
    PAYMENT_TRANSACTION_NOT_FOUND(3001, "payment transaction not found", HttpStatus.NOT_FOUND),
    INVALID_AMOUNT(3002, "invalid amount", HttpStatus.BAD_REQUEST),
    PAYMENT_PROVIDER_ERROR(3003, "payment provider error", HttpStatus.BAD_REQUEST),
    PAYMENT_WEBHOOK_INVALID(3004, "payment webhook invalid", HttpStatus.BAD_REQUEST),
    INSUFFICIENT_BALANCE(3005, "INCOMPLETE BALANCE", HttpStatus.BAD_REQUEST ),
    PROVIDER_NOT_FOUND(3006, "provider not found", HttpStatus.NOT_FOUND),
    PAYMENT_INFO_NOT_FOUND(3007, "không tìm thấy payment info này", HttpStatus.NOT_FOUND),
    INVALID_BANK_INFO(3008, "invalid bank info", HttpStatus.BAD_REQUEST),
    INVALID_PAYMENT_PROVIDER(3009, "invalid payment provider", HttpStatus.BAD_REQUEST),
    INVALID_CURRENCY_FOR_PROVIDER(3010, "invalid currency for provider", HttpStatus.BAD_REQUEST),
    INVALID_PAYPAL_EMAIL(3011, "invalid paypal email", HttpStatus.BAD_REQUEST),
    PAYMENT_INFO_ALREADY_EXISTS(3012, "payment ìno already exists", HttpStatus.BAD_REQUEST),
    WALLET_NOT_FOUND(3013, "wallet not found", HttpStatus.NOT_FOUND),
    BANK_EXISTED(3014, "bank existed", HttpStatus.BAD_REQUEST),
    INVALID_ORDER_STATUS(3015, "invalid order status", HttpStatus.BAD_REQUEST),
    ORDER_ALREADY_PAID(3016, "order already paid", HttpStatus.BAD_REQUEST),
    INVALID_STATUS(3017, "invalid status", HttpStatus.BAD_REQUEST),
    PAYMENT_INFO_CURRENCY_ALREADY_EXISTS(3018, "PAYMENT INFO CURRENCY ALREADY EXISTS", HttpStatus.BAD_REQUEST),
    ORDER_CAN_NOT_DELETE(3019, "Không thể xoá đơn hàng đã thanh toán hoặc đã hoàn thành!", HttpStatus.BAD_REQUEST),
    TRANSACTION_NOT_FOUND(3020, "TRANSACTION_NOT_FOUND", HttpStatus.BAD_REQUEST),
    OUT_OF_STOCK(3021, "OUT OF STOCK", HttpStatus.BAD_REQUEST),
    PAYMENT_SUCCESS(3022, "Giao dịch đã thành công rồi.", HttpStatus.BAD_REQUEST),
    PAYMENT_FAIL(3023, "Giao dịch đã bị huỷ/thất bại.", HttpStatus.BAD_REQUEST),
    PAYMENT_PROCESSING(3024, "Giao dịch đang được admin phê duyệt", HttpStatus.PROCESSING),

    NOT_ENOUGH_INVENTORY(3025, "Kho hàng không đủ số lượng để đáp ứng yêu cầu.", HttpStatus.BAD_REQUEST),
    QUANTITY_BELOW_MIN(3026, "Số lượng mua thấp hơn mức tối thiểu cho phép của sản phẩm.", HttpStatus.BAD_REQUEST),
    QUANTITY_ABOVE_MAX(3027, "Số lượng mua vượt quá mức tối đa cho phép của sản phẩm.", HttpStatus.BAD_REQUEST),
    CANNOT_BUY_OWN_PRODUCT(3028, "Không thể tự mua sản phẩm của chính mình.", HttpStatus.BAD_REQUEST),
    UNSUPPORTED_CURRENCY(3029, "Sản phẩm không hỗ trợ thanh toán bằng loại tiền tệ này.", HttpStatus.BAD_REQUEST),

    AMOUNT_TOO_SMALL(3030, "The amount is too small", HttpStatus.BAD_REQUEST),

    EXCEL_FAIL(3040, "Lỗi hệ thống khi tạo file Excel", HttpStatus.BAD_REQUEST),

    METHOD_NOT_ALLOWED(3050, "chưa hỗ trợ usd", HttpStatus.BAD_REQUEST),
    CANT_CREATE_WALLET(3060, "Cannot create or lock wallet", HttpStatus.BAD_REQUEST),

    ORDER_NOT_WAIT_PAYMENT(3070, "Đơn hàng không ở trạng thái chờ thanh toán.", HttpStatus.BAD_REQUEST),
    ORDER_FREE(3071, "Đơn hàng miễn phí không cần thanh toán qua ví.", HttpStatus.BAD_REQUEST),
    ORDER_TX_NOTFOUND(3072, "Order not found for transaction", HttpStatus.BAD_REQUEST),

    BANK_TIMEOUT(3080, "Không được xóa thẻ mới xác thực dưới 3 ngày", HttpStatus.BAD_REQUEST),
    VERIFY_FAIL(3081, "xác thực thất bại, tài khoản không tồn tại", HttpStatus.BAD_REQUEST),
    QUOTA_CHECK_END(3082, "Bạn đã hết lượt kiểm tra. Vui lòng xóa thẻ này và thêm lại nếu nhập sai.", HttpStatus.BAD_REQUEST),
    ONLY_BANK(3083, "Chỉ có thể kiểm tra tài khoản ngân hàng", HttpStatus.BAD_REQUEST),

    ORDER_REFUND_FAIL(3084, "Đơn hàng này đã huỷ hoặc đã được hoàn trả", HttpStatus.BAD_REQUEST),
    CANT_REFUND_BY_SELLER(3085, "Người bán chỉ có thể hoàn tiền cho đơn hàng chưa giao (PAID).", HttpStatus.BAD_REQUEST),
    // 3000 - 3100: product
    PRODUCT_NOT_FOUND(1008, "Sản phẩm không tồn tại", HttpStatus.NOT_FOUND),
    CATEGORY_NOT_FOUND(1009, "Danh mục không tồn tại", HttpStatus.NOT_FOUND),
    PRODUCT_UNAVAILABLE(1010, "Sản phẩm không khả dụng hoặc đã bị ẩn", HttpStatus.FORBIDDEN),
    SLUG_EXISTED(1011, "Slug đã tồn tại", HttpStatus.BAD_REQUEST),
    SKU_EXISTED(1012, "SKU đã tồn tại", HttpStatus.BAD_REQUEST),
    SELLER_NOT_REGISTERED(1013, "Bạn chưa đăng ký tài khoản người bán", HttpStatus.BAD_REQUEST),
    PRODUCT_NULL(1004, "product can not null", HttpStatus.BAD_REQUEST),
    CATEGORY_HAS_PRODUCTS(1005, "Không thể xóa! Danh mục này đang chứa sản phẩm.", HttpStatus.BAD_REQUEST),
    PRODUCT_MIN_MAX(1006, "Số lượng mua tối thiểu không được lớn hơn số lượng mua tối đa!", HttpStatus.BAD_REQUEST),
    PRODUCT_PRICE_EMPTY(1007, "Sản phẩm phải có ít nhất 1 mức giá!", HttpStatus.BAD_REQUEST),
    PRODUCT_NOT_DELETED(1008, "Sản phẩm này chưa bị xoá!", HttpStatus.BAD_REQUEST),
    // 4000 - 4099: ROLE
    ROLE_NOT_FOUND(4000, "role not found", HttpStatus.NOT_FOUND),
    USER_ROLE_LOWEST(4001, "user role lowest", HttpStatus.BAD_REQUEST),
    REQUEST_FAIL(4002, "request fail", HttpStatus.BAD_REQUEST),
    SELLER_EXISTED(4003, "this person was already a seller", HttpStatus.BAD_REQUEST),
    SELLER_REQUEST_EXISTED(4004, "seller request existed", HttpStatus.BAD_REQUEST),
    SELLER_REQUEST_NOT_FOUND(4005, "seller request not found", HttpStatus.NOT_FOUND),
    CANCEL_SELLER_FORBIDDEN(4006, "Bạn không có quyền hủy yêu cầu của người dùng khác.", HttpStatus.FORBIDDEN),
    SELLER_REQUEST_NOT_PENDING(4007, "Yêu cầu seller không ở trạng thái chờ duyệt.", HttpStatus.BAD_REQUEST),
    SELLER_CANNOT_DELETE(4008, "seller cannot delete", HttpStatus.BAD_REQUEST),
    SELLER_REJECTED(4009, "seller rejected", HttpStatus.BAD_REQUEST),
    INVALID_SELLER_STATUS(4010, "invalid seller status", HttpStatus.BAD_REQUEST),
    USER_ALREADY_DELETED(4011, "user already deleted", HttpStatus.BAD_REQUEST),

    // 4100 - 4199: shop
    OWNER_SHOP_NOT_FOUND(4100, "Shop không có chủ sở hữu hợp lệ!", HttpStatus.BAD_REQUEST),
    SHOP_NOT_FOUND(4101, "không tìm thấy shop", HttpStatus.NOT_FOUND),
    UNAUTHORIZED_SHOP_ACCESS(4102, "UNAUTHORIZED SHOP ACCESS", HttpStatus.BAD_REQUEST),
    AREADY_STAFF(4103, "Người dùng này đã là thành viên của gian hàng.", HttpStatus.BAD_REQUEST),
    // 5000 - 5099: Notification
    NOTIFICATION_NOT_EXISTED(5000, "notification not existed", HttpStatus.NOT_FOUND),
    NOTIFICATION_FORBIDEN(5001, "you don't have permission for this notification", HttpStatus.BAD_REQUEST),
    BROADCAST_NOT_FOUND(5002, "broadcast not found", HttpStatus.NOT_FOUND),
    // 5100 - 5199: affiliate
    APPLY_AFFILIATE_FAIL(5100, "Không thể tự thiết lập hoa hồng cho chính mình.", HttpStatus.BAD_REQUEST),


    // 6000 - 6099: Message
    MESSAGE_NOT_EXISTED(6000, "message not existed", HttpStatus.NOT_FOUND),

    // 6100 - 6199: complain
    REPORT_EXISTED(6100, "Bạn đã gửi khiếu nại rồi, vui lòng chờ người bán phản hồi!", HttpStatus.BAD_REQUEST),

    // 7000 - 7099: Validation
    VALIDATION_ERROR(7000, "validation error", HttpStatus.BAD_REQUEST),
    EMAIL_INCORRECT_FORMAT(7001, "email not correct format", HttpStatus.BAD_REQUEST),
    PASSWORD_TOO_WEAK(7002, "password too weak", HttpStatus.BAD_REQUEST),
    PASSWORD_REQUIRED(7003, "password required", HttpStatus.BAD_REQUEST),
    PASSWORD_TOO_SHORT(7004, "password too short", HttpStatus.BAD_REQUEST),
    PASSWORD_INVALID_FORMAT(7005, "pasword invalid format", HttpStatus.BAD_REQUEST),
    PASSWORD_CONFIRM_INCORRECT(7006, "password confirm incorrect", HttpStatus.BAD_REQUEST),
    INVALID_PASSWORD_INPUT(7007, "invalid password input", HttpStatus.BAD_REQUEST),
    INVALID_VALUE_INPUT(7008, "Giá trị phần trăm VAT phải là số", HttpStatus.BAD_REQUEST),
    INVALID_VALUE(7009, "Giá trị phải là số", HttpStatus.BAD_REQUEST),

    // 7100 - 7199: otp
    OTP_NOT_FOUND(7100, "otp not found", HttpStatus.NOT_FOUND),
    USER_VERIFIED(7101, "user verified", HttpStatus.BAD_REQUEST),
    USER_BANNED(7102, "user banned", HttpStatus.BAD_REQUEST),
    USER_NOT_VERIFIED(7103, "user not verified", HttpStatus.BAD_REQUEST),
    OTP_INVALID(7104, "Mã OTP không chính xác", HttpStatus.BAD_REQUEST),
    OTP_EXPIRED(7105, "Mã OTP đã hết hạn", HttpStatus.BAD_REQUEST),
    USER_ALREADY_VERIFIED(7106, "Tài khoản đã được xác thực trước đó", HttpStatus.BAD_REQUEST),

    // 7200 - 7299: coupon
    COUPON_EXISTED(7200, "Mã giảm giá đã tồn tại trong hệ thống", HttpStatus.BAD_REQUEST),
    COUPON_NOT_EXISTED(7201, "Mã giảm giá không tồn tại", HttpStatus.NOT_FOUND),
    COUPON_INACTIVE(7202, "Mã giảm giá không hoạt động", HttpStatus.BAD_REQUEST),
    COUPON_EXPIRED(7203, "Mã giảm giá đã hết hạn", HttpStatus.BAD_REQUEST),
    COUPON_USAGE_LIMIT_REACHED(7204, "Mã giảm giá đã hết lượt sử dụng", HttpStatus.BAD_REQUEST),
    COUPON_INVALID_CURRENCY(7205, "Mã giảm giá không áp dụng cho loại tiền tệ này", HttpStatus.BAD_REQUEST),
    COUPON_INVALID_PRODUCT(7206, "Mã giảm giá không áp dụng cho sản phẩm này", HttpStatus.BAD_REQUEST),
    COUPON_INVALID_SELLER(7207, "Mã giảm giá không áp dụng cho shop này", HttpStatus.BAD_REQUEST),

    // 8000 - 8100: file
    FILE_UPLOAD_ERROR(8000, "file upload error", HttpStatus.BAD_REQUEST),

    // 9000 - 9099: Auth & General
    UNAUTHORIZED(9000, "not have permission", HttpStatus.FORBIDDEN),
    TOKEN_INVALID(9001, "token is not valid", HttpStatus.BAD_REQUEST),
    INTERNAL_SERVER_ERROR(9002, "internal server error", HttpStatus.INTERNAL_SERVER_ERROR),
    DUPLICATE_RESOURCE(9003, "duplicate resource", HttpStatus.BAD_REQUEST),
    INVALID_KEY(9004, "invalid message key", HttpStatus.BAD_REQUEST),
    REFRESH_TOKEN_IS_MISSING(9005, "refresh token is missiong", HttpStatus.NOT_FOUND),
    UNAUTHENTICATED(9006, "Không có quyền truy cập. Vui lòng đăng nhập.", HttpStatus.UNAUTHORIZED),
    ACCESS_DENIED(9007, "Bạn không có quyền thực hiện hành động này.", HttpStatus.FORBIDDEN),

    UNCATEGORIZED_EXCEPTION(9999, "uncategorized exception", HttpStatus.INTERNAL_SERVER_ERROR);
    private int code;
    private String message;
    private HttpStatusCode statusCode;

    ErrorCode(int code, String message, HttpStatusCode statusCode) {
        this.code = code;
        this.message = message;
        this.statusCode = statusCode;
    }
}
