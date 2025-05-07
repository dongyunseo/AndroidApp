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
mysqli_set_charset($conn, "utf8");

if ($conn->connect_error) {
    echo json_encode(array("error" => "DB 연결 실패"));
    exit;
}

// 안드로이드에서 전달받은 userId 받기 (POST 우선)
$userid = isset($_POST['userId']) ? $_POST['userId'] : (isset($_GET['userId']) ? $_GET['userId'] : '');

if (empty($userid)) {
    echo json_encode(array("error" => "userId Not Provided"));
    exit;
}

// SQL 쿼리 - 각 모델에 대해 알림 건수를 함께 집계
$sql = "
SELECT 
    a.Full_Model_Name as Full_Model_Name,
    a.Price as Price,
    a.reg_date as reg_date,
    COUNT(cp.Full_Model_Name) AS AlertCount
FROM 
    alarm a
LEFT JOIN 
    Car_Price cp
    ON a.Full_Model_Name = cp.Full_Model_Name
    AND CAST(REPLACE(cp.Price, ',', '') AS UNSIGNED) < CAST(REPLACE(a.Price, ',', '') AS UNSIGNED)
WHERE 
    a.userId = ?
GROUP BY 
    a.Full_Model_Name, a.Price, a.reg_date
ORDER BY 
    a.reg_date DESC;
";

// 쿼리 실행
$stmt = $conn->prepare($sql);
$stmt->bind_param("s", $userid);
$stmt->execute();
$result = $stmt->get_result();

// 결과 처리
$alarm_list = array();
while ($row = $result->fetch_assoc()) {
    $alarm_list[] = $row;
}

// JSON 응답
header('Content-Type: application/json; charset=utf-8');
echo json_encode($alarm_list, JSON_UNESCAPED_UNICODE);

// 마무리
$stmt->close();
$conn->close();
?>
