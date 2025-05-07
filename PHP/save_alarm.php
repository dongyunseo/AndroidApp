<?php
// 데이터베이스 연결 설정
$servername = "test";
$username = "test"; // MySQL 사용자명
$password = "test"; // MySQL 비밀번호
$dbname = "test"; // 사용하려는 데이터베이스 이름

$conn = new mysqli($servername, $username, $password, $dbname);
if ($conn->connect_error) {
    die("DB 연결 실패: " . $conn->connect_error);
}

$userId = $_POST['userId'];
$fullModelName = $_POST['Full_Model_Name'];
$price = $_POST['Price'];
$useYN = $_POST['Use_YN'];

$sql = "INSERT INTO alarm (userId, Full_Model_Name, Price, Use_YN)
        VALUES (?, ?, ?, ?)
        ON DUPLICATE KEY UPDATE Use_YN = VALUES(Use_YN)";

$stmt = $conn->prepare($sql);
$stmt->bind_param("ssss", $userId, $fullModelName, $price, $useYN);
$stmt->execute();

echo "저장 완료";
$conn->close();
?>
