<?php
header('Content-Type: application/json');

// 데이터베이스 연결 설정
$servername = "test";
$username = "test"; // MySQL 사용자명
$password = "test"; // MySQL 비밀번호
$dbname = "test"; // 사용하려는 데이터베이스 이름

// MySQL 연결
$conn = new mysqli($servername, $username, $password, $dbname);

if ($conn->connect_error) {
    die("연결 실패: " . $conn->connect_error);
}

$customer_id = isset($_POST['customer_id']) ? $_POST['customer_id'] : '';

if (empty($customer_id)) {
    echo json_encode(["success" => false, "message" => "아이디를 입력하세요."]);
    exit;
}

$stmt = $conn->prepare("SELECT customer_id FROM Customer WHERE customer_id = ?");
$stmt->bind_param("s", $customer_id);
$stmt->execute();
$stmt->store_result();

if ($stmt->num_rows > 0) {
    echo json_encode([
        "isDuplicate" => true,   // 중복 아이디가 있을 경우 true
        "message" => "이미 사용중인 아이디입니다."
    ]);
} else {
    echo json_encode([
        "isDuplicate" => false,  // 중복 아이디가 없을 경우 false
        "message" => "사용 가능한 아이디입니다."
    ]);
}



$stmt->close();
$conn->close();
?>
