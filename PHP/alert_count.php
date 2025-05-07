<?php
ini_set('display_errors', 1);
error_reporting(E_ALL);

// 데이터베이스 연결 설정
$servername = "localhost";
$username = "sdy9716"; // MySQL 사용자명
$password = "dongyun2766"; // MySQL 비밀번호
$dbname = "sdy9716"; // 데이터베이스 이름

// DB 연결
$conn = new mysqli($servername, $username, $password, $dbname);

// 연결 오류 시 종료
if ($conn->connect_error) {
    die("Connection failed: " . $conn->connect_error);
}

// 클라이언트로부터 전달받은 userId
$userId = $_POST['userId'] ?? '';

if (empty($userId)) {
    echo json_encode(['error' => 'userId is required']);
    exit;
}

// SQL: 알림 가격보다 더 싼 매물이 있는 경우만 count
$sql = "
SELECT COUNT(*) AS alert_count
FROM Car_Price cp
JOIN alarm a ON cp.Full_Model_Name = a.Full_Model_Name
WHERE cp.Price < a.Price
AND a.UserId = ?
AND a.Use_YN = 'Y'
";

// SQL 실행
$stmt = $conn->prepare($sql);
$stmt->bind_param("s", $userId);
$stmt->execute();
$result = $stmt->get_result();
$row = $result->fetch_assoc();

// 결과 반환
echo json_encode(['alert_count' => $row['alert_count']]);

// 자원 해제
$stmt->close();
$conn->close();
?>
