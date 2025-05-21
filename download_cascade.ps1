$url = "https://raw.githubusercontent.com/opencv/opencv/master/data/haarcascades/haarcascade_frontalface_default.xml"
$output = "src/main/resources/haarcascades/haarcascade_frontalface_default.xml"

# Создаем директорию, если она не существует
New-Item -ItemType Directory -Force -Path "src/main/resources/haarcascades"

# Загружаем файл
Invoke-WebRequest -Uri $url -OutFile $output

Write-Host "Каскадный классификатор успешно загружен в $output" 