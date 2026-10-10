# Skin Selector (Fabric 1.21.11)

При нажатии «Create New World» появляется окно выбора персонажа в стиле Bedrock:
стрелки < > листают Sunny, Kai, Makena, Steve, Alex, Zuri, Efe, Ari, Noor;
кнопки Wide / Slim переключают вариант скина; Confirm подтверждает и создаёт мир; Esc — отмена.

Выбор запоминается для каждого мира отдельно (config/skinselector.json).
Скин виден только вам и только в одиночной игре (мод клиентский).

## Сборка через GitHub (ничего устанавливать не нужно)
1. Создайте аккаунт на github.com и новый репозиторий (New repository).
2. Нажмите «uploading an existing file» и перетащите ВСЕ файлы и папки проекта
   (включая скрытую папку `.github`). Нажмите Commit changes.
   Если папку `.github` не получается перетащить, используйте GitHub Desktop или `git push`.
3. Откройте вкладку **Actions**. Сборка запустится сама (или нажмите Build mod -> Run workflow).
4. Через 2-4 минуты откройте завершённый запуск (зелёная галочка), внизу в разделе
   **Artifacts** скачайте `skinselector-mod` - внутри готовый `skinselector-1.0.0.jar`.
5. Положите jar в папку `mods` вместе с Fabric API (Minecraft 1.21.11, Fabric Loader 0.18.1+).

Если сборка красная, откройте её лог и пришлите ошибку.

## Сборка локально
Нужны JDK 21 и Gradle 8.14+: `gradle build`, jar появится в `build/libs/`.
Версии в `gradle.properties` при необходимости обновите (https://fabricmc.net/develop).
