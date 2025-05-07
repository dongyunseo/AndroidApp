<?php
// 데이터베이스 연결 설정
$servername = "test";
$username = "test"; // MySQL 사용자명
$password = "test"; // MySQL 비밀번호
$dbname = "test"; // 사용하려는 데이터베이스 이름
// DB 연결
$conn = new mysqli($servername, $username, $password, $dbname);
mysqli_set_charset($conn, "utf8");

// 연결 확인
if ($conn->connect_error) {
    die("연결 실패: " . $conn->connect_error);
}

// 안드로이드에서 GET으로 받은 모델 이름
$model_name = isset($_POST['Model_Name']) ? $_POST['Model_Name'] : '';


// 모델 이름이 없는 경우 처리

if (isset($_POST['Model_Name'])) {
    $model_name = $_POST['Model_Name'];
    // 계속해서 처리
} else {
    echo json_encode(["error" => "모델 이름이 전달되지 않았습니다."]);
}



// 디버깅: 받은 모델 이름 로그 확인
error_log("Received Model Name: " . $model_name);

// SQL 실행
$sql = "SELECT 
            Full_Model_Name,
            Car_Img_URL,
            Price,
            Year,
            Mileage,
            Fuel_Type,
            Location
        FROM Car_Price 
        WHERE Model_Name = ?
        ORDER BY Price DESC";

// SQL 준비 및 실행
$stmt = $conn->prepare($sql);
$stmt->bind_param("s", $model_name);
$stmt->execute();
$result = $stmt->get_result();

// 결과 처리
$car_list = array();
while ($row = $result->fetch_assoc()) {
    $car_list[] = $row;
}

// JSON 형식으로 응답
header('Content-Type: application/json; charset=utf-8');
echo json_encode($car_list, JSON_UNESCAPED_UNICODE);

// 마무리
$stmt->close();
?>
