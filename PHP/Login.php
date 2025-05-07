<?php
// MySQL 데이터베이스 연결 설정
// 데이터베이스 연결 설정
$servername = "test";
$username = "test"; // MySQL 사용자명
$password = "test"; // MySQL 비밀번호
$dbname = "test"; // 사용하려는 데이터베이스 이름

$conn = new mysqli($host, $user, $pass, $dbname);

// 연결 확인
if ($conn->connect_error) {
    die("연결 실패: " . $conn->connect_error);
}

// 클라이언트로부터 전달받은 값
$id = $_POST['id'];
$pw = $_POST['pw'];

// SQL 인젝션 방지
$id = mysqli_real_escape_string($conn, $id);

// Customer 테이블에서 해당 ID에 대한 정보 조회
$sql = "SELECT password FROM Customer WHERE customer_id = '$id'";
$result = $conn->query($sql);

if ($result->num_rows > 0) {
    $row = $result->fetch_assoc();
    $hashedPw = $row['password'];

    // bcrypt 해시 비교
    if (password_verify($pw, $hashedPw)) {
        echo "success";
    } else {
        echo "fail"; // 비밀번호 틀림
    }
} else {
    echo "fail"; // 아이디 없음
}

$conn->close();
?>
