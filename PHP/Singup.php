<?php
// 데이터베이스 연결 설정
$servername = "test";
$username = "test"; // MySQL 사용자명
$password = "test"; // MySQL 비밀번호
$dbname = "test"; // 사용하려는 데이터베이스 이름

// 데이터베이스 연결
$conn = new mysqli($servername, $username, $password, $dbname);

// 연결 체크
if ($conn->connect_error) {
    die("Connection failed: " . $conn->connect_error);
}

// POST로 전달된 데이터 받기
$user_id = $_POST['user_id'];
$password = $_POST['password'];
$name = $_POST['name'];
$birth_date = $_POST['birth_date'];
$city = $_POST['city'];
$district = $_POST['district'];
$phone = $_POST['phone'];
$gender = $_POST['gender']; // 성별 추가

// 비밀번호 해싱
$hashed_password = password_hash($password, PASSWORD_DEFAULT);

// SQL 쿼리 작성 - 회원가입
$sql = "INSERT INTO Customer (customer_id, password, name, birth_date, city, district, phone, gender) 
        VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

$stmt = $conn->prepare($sql);
$stmt->bind_param("ssssssss", $user_id, $hashed_password, $name, $birth_date, $city, $district, $phone, $gender);

if ($stmt->execute()) {
    $response = array("success" => true, "message" => "회원가입 성공");
} else {
    $response = array("success" => false, "message" => "회원가입 실패");
}

// 응답 반환
echo json_encode($response);

// 연결 종료
$conn->close();
?>
