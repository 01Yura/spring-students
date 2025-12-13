#!/bin/bash
# Диагностический скрипт для поиска причины 404 ошибки

echo "=========================================="
echo "Диагностика 404 ошибки ingress-nginx"
echo "=========================================="
echo ""

echo "1. Проверка Ingress ресурса:"
echo "----------------------------------------"
kubectl get ingress -n default spring-students-ingress
echo ""
kubectl describe ingress -n default spring-students-ingress | grep -A 10 "Rules:"
echo ""

echo "2. Проверка Service:"
echo "----------------------------------------"
kubectl get svc -n default spring-students-app
echo ""
kubectl describe svc -n default spring-students-app | grep -A 5 "Endpoints:"
echo ""

echo "3. Проверка Endpoints:"
echo "----------------------------------------"
kubectl get endpoints -n default spring-students-app
echo ""
kubectl describe endpoints -n default spring-students-app
echo ""

echo "4. Проверка подов приложения:"
echo "----------------------------------------"
kubectl get pods -n default -l app=spring-students-app
echo ""

echo "5. Проверка ingress-nginx подов:"
echo "----------------------------------------"
kubectl get pods -n ingress-nginx -l app.kubernetes.io/name=ingress-nginx
echo ""

echo "6. Проверка конфигурации nginx внутри пода ingress-nginx:"
echo "----------------------------------------"
INGRESS_POD=$(kubectl get pods -n ingress-nginx -l app.kubernetes.io/name=ingress-nginx -o jsonpath='{.items[0].metadata.name}')
echo "Используем под: $INGRESS_POD"
echo ""
echo "Поиск конфигурации для spring-students-app:"
kubectl exec -n ingress-nginx $INGRESS_POD -- cat /etc/nginx/nginx.conf 2>/dev/null | grep -A 30 "spring-students" || echo "Конфигурация не найдена"
echo ""

echo "7. Проверка доступности сервиса из пода ingress-nginx:"
echo "----------------------------------------"
kubectl exec -n ingress-nginx $INGRESS_POD -- curl -s -o /dev/null -w "%{http_code}" http://spring-students-app.default.svc.cluster.local:8081/actuator/health 2>/dev/null || echo "Не удалось подключиться"
echo ""

echo "8. Проверка логов ingress-nginx (последние 20 строк):"
echo "----------------------------------------"
kubectl logs -n ingress-nginx -l app.kubernetes.io/name=ingress-nginx --tail=20 | grep -E "(404|error|spring-students)" || echo "Нет релевантных записей"
echo ""

echo "9. Проверка событий Ingress:"
echo "----------------------------------------"
kubectl get events -n default --field-selector involvedObject.name=spring-students-ingress --sort-by='.lastTimestamp' | tail -10
echo ""

echo "10. Проверка LoadBalancer IP:"
echo "----------------------------------------"
kubectl get svc -n ingress-nginx ingress-nginx-controller
echo ""

echo "=========================================="
echo "Диагностика завершена"
echo "=========================================="

