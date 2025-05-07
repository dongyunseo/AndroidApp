<?php
ini_set('display_errors', 1);
error_reporting(E_ALL);
// 데이터베이스 연결 설정
$servername = "test";
$username = "test"; // MySQL 사용자명
$password = "test"; // MySQL 비밀번호
$dbname = "test"; // 사용하려는 데이터베이스 이름

// MySQL 연결
$conn = new mysqli($servername, $username, $password, $dbname);

if ($conn->connect_error) {
    echo json_encode(array("error" => "DB 연결 실패"));
    exit;
}

// POST 또는 GET으로부터 userId 받기
$customer_id = $_POST['userId'] ?? $_GET['user_Id'] ?? null;

if ($customer_id === null) {
    echo json_encode(array("error" => "userId 값이 전달되지 않았습니다."));
    exit;
}

// 쿼리 실행
$stmt = $conn->prepare("SELECT name, birth_date, concat(city,district) as address FROM Customer WHERE customer_id = ?");
$stmt->bind_param("s", $customer_id);
$stmt->execute();
$result = $stmt->get_result();

if ($result->num_rows > 0) {
    $row = $result->fetch_assoc();
    echo json_encode($row);
} else {
    echo json_encode(array("error" => "해당 회원 정보를 찾을 수 없습니다."));
}

$stmt->close();
$conn->close();
