<?php
// 데이터베이스 연결 설정
$servername = "test";
$username = "test"; // MySQL 사용자명
$password = "test"; // MySQL 비밀번호
$dbname = "test"; // 사용하려는 데이터베이스 이름

$conn = new mysqli($servername, $username, $password, $dbname);
if ($conn->connect_error) {
    die("연결 실패: " . $conn->connect_error);
}

$modelName = $_POST['Model_Name'];

$sql = "
    SELECT 
        Full_Model_Name,
        MIN(Price) as Price
    FROM Car_Price 
    WHERE 1=1
      AND Model_Name = ?
    GROUP BY Full_Model_Name
";

$stmt = $conn->prepare($sql);
$stmt->bind_param("s", $modelName);
$stmt->execute();

$result = $stmt->get_result();
$data = [];

while ($row = $result->fetch_assoc()) {
    $data[] = $row;
}

echo json_encode($data);
$conn->close();
?>
