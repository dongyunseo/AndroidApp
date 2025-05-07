<?php
// DB 연결
$servername = "localhost";
$username = "sdy9716";
$password = "dongyun2766";
$dbname = "sdy9716";

$conn = new mysqli($servername, $username, $password, $dbname);
mysqli_set_charset($conn, "utf8");

if ($conn->connect_error) {
    die("DB 연결 실패: " . $conn->connect_error);
}

// 안드로이드에서 전달한 제조사명 받기
$manufacturer_name = $_POST['manufacturer_name'] ?? $_GET['manufacturer_name'] ?? '';

if (empty($manufacturer_name)) {
    echo json_encode(["error" => "manufacturer_name이 전달되지 않았습니다."]);
    exit;
}

// 쿼리 실행
$sql = "
SELECT 
    A.car_name AS carName, 
    A.img_url AS imgUrl 
FROM 
    Car_Img_File A, Car_Price B
WHERE 
    A.manufacturer_name = ?
    AND A.car_name = B.Model_Name
GROUP BY 
    A.car_name, A.img_url;

";

$stmt = $conn->prepare($sql);
$stmt->bind_param("s", $manufacturer_name);
$stmt->execute();
$result = $stmt->get_result();

$response = [];

while ($row = $result->fetch_assoc()) {
    $response[] = $row;
}

echo json_encode($response, JSON_UNESCAPED_UNICODE);

$stmt->close();
$conn->close();
?>
