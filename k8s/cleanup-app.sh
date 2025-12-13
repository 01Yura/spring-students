#!/bin/bash
# Скрипт для удаления ресурсов приложения Spring Students
# Удаляет только Ingress, Service и Deployment приложения
# Не трогает инфраструктурные компоненты (ingress-nginx, MetalLB)

set -e

echo "=========================================="
echo "Удаление ресурсов приложения Spring Students"
echo "=========================================="
echo ""

# Проверяем, что kubectl доступен
if ! command -v kubectl &> /dev/null; then
    echo "Ошибка: kubectl не найден. Убедитесь, что kubectl установлен и доступен."
    exit 1
fi

echo "Текущие ресурсы приложения:"
echo "----------------------------------------"
kubectl get ingress,svc,deployment -n default -l app=spring-students-app 2>/dev/null || echo "Ресурсы не найдены"
echo ""

# Подтверждение удаления
read -p "Вы уверены, что хотите удалить все ресурсы приложения? (yes/no): " confirm
if [ "$confirm" != "yes" ]; then
    echo "Отменено."
    exit 0
fi

echo ""
echo "Удаление ресурсов..."
echo "----------------------------------------"

# Удаляем Ingress
echo "1. Удаление Ingress..."
if kubectl get ingress -n default spring-students-ingress &>/dev/null; then
    kubectl delete ingress -n default spring-students-ingress
    echo "   ✓ Ingress удален"
else
    echo "   ⚠ Ingress не найден (возможно, уже удален)"
fi

# Удаляем Service
echo "2. Удаление Service..."
if kubectl get svc -n default spring-students-app &>/dev/null; then
    kubectl delete svc -n default spring-students-app
    echo "   ✓ Service удален"
else
    echo "   ⚠ Service не найден (возможно, уже удален)"
fi

# Удаляем Deployment (это также удалит все поды)
echo "3. Удаление Deployment..."
if kubectl get deployment -n default spring-students-app &>/dev/null; then
    kubectl delete deployment -n default spring-students-app
    echo "   ✓ Deployment удален (все поды также будут удалены)"
else
    echo "   ⚠ Deployment не найден (возможно, уже удален)"
fi

# Ждем завершения удаления подов
echo ""
echo "Ожидание завершения удаления подов..."
kubectl wait --for=delete pod -n default -l app=spring-students-app --timeout=60s 2>/dev/null || true

echo ""
echo "=========================================="
echo "Проверка результата"
echo "=========================================="
echo ""

# Проверяем, что все удалено
REMAINING=$(kubectl get ingress,svc,deployment,pod -n default -l app=spring-students-app 2>/dev/null | wc -l)
if [ "$REMAINING" -le 1 ]; then
    echo "✓ Все ресурсы приложения успешно удалены"
else
    echo "⚠ Обнаружены оставшиеся ресурсы:"
    kubectl get ingress,svc,deployment,pod -n default -l app=spring-students-app
fi

echo ""
echo "Инфраструктурные компоненты (не удалены):"
echo "----------------------------------------"
echo "ingress-nginx:"
kubectl get pods -n ingress-nginx 2>/dev/null | head -3 || echo "  Не найден"
echo ""
echo "MetalLB:"
kubectl get pods -n metallb-system 2>/dev/null | head -3 || echo "  Не найден"
echo ""

echo "=========================================="
echo "Готово! Теперь можно запустить пайплайн для пересоздания ресурсов."
echo "=========================================="

