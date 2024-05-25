
# Ứng dụng Quán Cafe Online

## Giới thiệu

Ứng dụng quán cafe online là một giải pháp hoàn chỉnh để quản lý quán cafe và tương tác với khách hàng. Ứng dụng hỗ trợ các tính năng như đặt hàng trực tuyến, quản lý giỏ hàng, theo dõi đơn hàng, và nhiều tính năng quản lý cho admin.

## Công nghệ sử dụng

- **Java (AndroidX, Google Material)**
- **Room Database**: Quản lý offline giỏ hàng
- **Firebase Realtime Database**: Cơ sở dữ liệu thời gian thực
- **EventBus Library**: Xử lý sự kiện
- **Firebase Authentication**: Xác thực người dùng
- **Glide**: Tải ảnh từ URL
- **Firebase Storage**: Lưu trữ ảnh sản phẩm
- **CircleImageView, CircleIndicator, Material Dialogs, Gson**
- **Shared Preferences Android**: Lưu trạng thái đăng nhập của người dùng
- **MPAndroidChart**: Vẽ biểu đồ

## Tính năng

### User

- Đăng nhập/Quên mật khẩu/Đổi mật khẩu/Đăng ký bằng email
- Danh sách đồ uống (có bộ lọc)
- Hiển thị Slide Image: Tự động chuyển slide
- Tìm kiếm đồ uống theo tên
- Hiển thị thông tin chi tiết đồ uống
- Danh sách đơn hàng (đang xử lý/đã hoàn thành)
- Giỏ hàng (thêm, xóa, thay đổi số lượng, tùy chỉnh (size, topping, ice, ...))
- Đặt hàng:
  - Chọn phương thức thanh toán
  - Chọn địa chỉ (CRUD)
  - Chọn voucher
  - Sau khi đặt hàng: hiển thị hóa đơn, gửi thông báo
- Theo dõi đơn hàng qua các trạng thái (Quán nhận đơn, Chuẩn bị đơn, Đơn hàng đã hoàn tất: gửi thông báo)
- Xếp hạng/đánh giá đồ uống, xếp hạng/đánh giá đơn hàng
- Feedback: Gửi phản hồi/đóng góp ý kiến/đánh giá ứng dụng
- Hiển thị contact với quán qua các kênh: Facebook, Zalo, Gmail, Phone, Youtube,…

### Admin

- Đăng nhập/Quên mật khẩu/Đổi mật khẩu
- Quản lý order (cập nhật trạng thái, tìm kiếm)
- Quản lý đồ uống (CRUD, tìm kiếm, bộ lọc)
- Quản lý voucher (CRUD, tìm kiếm)
- Quản lý khách hàng (tìm kiếm)
- Xem phản hồi
- Xem doanh thu (biểu đồ, thống kê doanh thu theo ngày/tuần/tháng/quý/năm/tùy chọn khoảng thời gian, xuất file pdf/word/excel)

## Cài đặt

1. Clone repository:
   ```sh
   git clone https://github.com/kienkienit/mobile.git
   ```
2. Điều hướng đến thư mục dự án:
   ```sh
   cd /path/to/your/project
   ```
3. Cấu hình Firebase:
   - Tạo project Firebase và thêm file `google-services.json` vào thư mục `app` của dự án.

4. Chạy ứng dụng:
   - Mở dự án bằng Android Studio và build ứng dụng.

## Đóng góp

Mọi đóng góp đều được chào đón! Bạn có thể fork dự án, tạo một nhánh mới và gửi pull request.

## Tác giả

Phùng Đức Kiên

