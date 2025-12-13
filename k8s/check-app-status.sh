#!/bin/bash
# Скрипт для проверки статуса приложения Spring Students

echo "=========================================="
echo "Проверка статуса приложения Spring Students"
echo "=========================================="
echo ""

# 1. Проверка Deployment
echo "1. Deployment:"
echo "----------------------------------------"
kubectl get deployment -n default spring-students-app 2>/dev/null || echo "⚠ Deployment не найден"
kubectl describe deployment -n default spring-students-app 2>/dev/null | grep -A 10 "Events:" || echo ""
echo ""

# 2. Проверка подов
echo "2. Поды приложения:"
echo "----------------------------------------"
kubectl get pods -n default -l app=spring-students-app
echo ""

# 3. Проверка Service
echo "3. Service:"
echo "----------------------------------------"
kubectl get svc -n default spring-students-app 2>/dev/null || echo "⚠ Service не найден"
echo ""

# 4. Проверка Ingress
echo "4. Ingress:"
echo "----------------------------------------"
kubectl get ingress -n default spring-students-ingress 2>/dev/null || echo "⚠ Ingress не найден"
echo ""

# 5. Проверка логов подов (если есть)
echo "5. Логи подов (последние 20 строк каждого пода):"
echo "----------------------------------------"
PODS=$(kubectl get pods -n default -l app=spring-students-app -o jsonpath='{.items[*].metadata.name}' 2>/dev/null)
if [ -z "$PODS" ]; then
    echo "⚠ Поды не найдены"
else
    for pod in $PODS; do
        echo ""
        echo "Логи пода: $pod"
        echo "---"
        kubectl logs -n default $pod --tail=20 2>&1 | tail -20
        echo ""
    done
fi

# 6. Проверка событий
echo "6. События (последние 10):"
echo "----------------------------------------"
kubectl get events -n default --sort-by='.lastTimestamp' | grep -E "(spring-students|Error|Warning)" | tail -10 || echo "Нет событий"
echo ""

# 7. Проверка статуса подов детально
echo "7. Детальный статус подов:"
echo "----------------------------------------"
kubectl get pods -n default -l app=spring-students-app -o wide
echo ""

# 8. Проверка описания проблемных подов
echo "8. Описание проблемных подов:"
echo "----------------------------------------"
for pod in $PODS; do
    STATUS=$(kubectl get pod -n default $pod -o jsonpath='{.status.phase}' 2>/dev/null)
    if [ "$STATUS" != "Running" ]; then
        echo ""
        echo "Под $pod (статус: $STATUS):"
        kubectl describe pod -n default $pod | grep -A 15 "Events:" || echo ""
    fi
done

echo ""
echo "=========================================="
echo "Проверка завершена"
echo "=========================================="

