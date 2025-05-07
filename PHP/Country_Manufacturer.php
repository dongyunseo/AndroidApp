<?php
// 데이터베이스 연결 설정
$servername = "test";
$username = "test"; // MySQL 사용자명
$password = "test"; // MySQL 비밀번호
$dbname = "test"; // 사용하려는 데이터베이스 이름

// MySQL 연결
$conn = new mysqli($servername, $username, $password, $dbname);

mysqli_set_charset($conn, "utf8");

$sql = "
SELECT B.Country as country, A.Manufacturer_Name as name, A.Logo_URL as logo
FROM Manufacturer_Logo A
JOIN Manufacturer_Info B ON A.Manufacturer_Name = B.Manufacturer_Name
ORDER BY 
  CASE WHEN B.Country = '대한민국' THEN 1 ELSE 2 END, B.Country;
";

$result = mysqli_query($conn, $sql);

$data = [];
while ($row = mysqli_fetch_assoc($result)) {
    $country = $row['country'];
    if (!isset($data[$country])) {
        $data[$country] = ["country" => $country, "manufacturers" => []];
    }
    $data[$country]["manufacturers"][] = [
        "name" => $row["name"],
        "logoURL" => $row["logo"]
    ];
}

echo json_encode(array_values($data), JSON_UNESCAPED_UNICODE);
?>
