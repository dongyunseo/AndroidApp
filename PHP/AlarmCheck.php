<?php
header("Content-Type: application/json; charset=UTF-8");

// 데이터베이스 연결 설정
$servername = "test";
$username = "test"; // MySQL 사용자명
$password = "test"; // MySQL 비밀번호
$dbname = "test"; // 사용하려는 데이터베이스 이름

$conn = new mysqli($servername, $username, $password, $dbname);
if ($conn->connect_error) {
    die(json_encode(["error" => "DB 연결 실패: " . $conn->connect_error]));
}

// 클라이언트로부터 받은 모델명 받기
$modelName = $_GET['modelName'] ?? '';

if (empty($modelName)) {
    echo json_encode(["error" => "modelName 파라미터 누락"]);
    exit;
}

$sql = "
    SELECT Full_Model_Name, Price, Mileage, Location
    FROM Car_Price A
    WHERE A.Full_Model_Name = ?
    AND A.Price = (
        SELECT MIN(B.Price)
        FROM Car_Price B
        WHERE B.Full_Model_Name = ?
    )
";

$stmt = $conn->prepare($sql);
$stmt->bind_param("ss", $modelName, $modelName);
$stmt->execute();
$result = $stmt->get_result();

$data = [];

while ($row = $result->fetch_assoc()) {
    $data[] = $row;
}

echo json_encode($data, JSON_UNESCAPED_UNICODE);

$conn->close();
?>
