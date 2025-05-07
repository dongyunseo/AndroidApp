<?php
header('Content-Type: application/json');
// 데이터베이스 연결 설정
$servername = "test";
$username = "test"; // MySQL 사용자명
$password = "test"; // MySQL 비밀번호
$dbname = "test"; // 사용하려는 데이터베이스 이름

// MySQL 연결
$conn = new mysqli($servername, $username, $password, $dbname);

// 연결 체크
if ($conn->connect_error) {
    die("Connection failed: " . $conn->connect_error);
}

// SQL 쿼리 실행
$sql = "SELECT B.Country as country
        FROM Manufacturer_Logo A, Manufacturer_Info B
        WHERE 1 = 1
          AND A.Manufacturer_Name = B.Manufacturer_Name
          AND A.Manufacturer_Name IN (
            SELECT C.Manufacturer
            FROM Car_Price C
            GROUP BY C.Manufacturer
          )
        GROUP BY B.Country
        ORDER BY 
          CASE 
            WHEN B.Country = '대한민국' THEN 1 
            ELSE 2 
          END, 
          B.Country";

$result = $conn->query($sql);

// 결과 배열 초기화
$countries = array();

// 결과가 있을 경우
if ($result->num_rows > 0) {
    // 각 행을 배열에 추가
    while($row = $result->fetch_assoc()) {
        $countries[] = $row['country'];
    }
}

// 결과를 JSON 형식으로 반환
echo json_encode($countries);

// MySQL 연결 종료
$conn->close();
?>
