# Диагностика проблем с Ingress

## Проверка статуса компонентов

### 1. Проверить, что Ingress создан и виден ingress-nginx:
```bash
kubectl get ingress -n default
kubectl describe ingress spring-students-ingress -n default
```

### 2. Проверить, что сервис существует:
```bash
kubectl get svc -n default spring-students-app
kubectl describe svc spring-students-app -n default
```

### 3. Проверить, что поды приложения работают:
```bash
kubectl get pods -n default -l app=spring-students-app
kubectl logs -n default -l app=spring-students-app --tail=20
```

### 4. Проверить логи ingress-nginx:
```bash
kubectl logs -n ingress-nginx -l app.kubernetes.io/name=ingress-nginx --tail=50
```

### 5. Проверить, что ingress-nginx видит Ingress ресурсы:
```bash
# Проверить события ingress-nginx
kubectl get events -n ingress-nginx --sort-by='.lastTimestamp'

# Проверить конфигурацию nginx внутри пода
kubectl exec -n ingress-nginx -it $(kubectl get pods -n ingress-nginx -l app.kubernetes.io/name=ingress-nginx -o jsonpath='{.items[0].metadata.name}') -- cat /etc/nginx/nginx.conf | grep -A 20 "spring-students"
```

### 6. Проверить, что ingress-nginx может подключиться к сервису:
```bash
# Из пода ingress-nginx попробовать подключиться к сервису
kubectl exec -n ingress-nginx -it $(kubectl get pods -n ingress-nginx -l app.kubernetes.io/name=ingress-nginx -o jsonpath='{.items[0].metadata.name}') -- curl http://spring-students-app.default.svc.cluster.local:8081/actuator/health
```

## Возможные проблемы и решения

### Проблема: 404 Not Found
**Причины:**
1. Ingress не видит сервис (разные namespace)
2. Сервис не существует или неправильное имя
3. Поды приложения не готовы
4. Неправильные правила маршрутизации в Ingress

**Решение:**
- Убедитесь, что Ingress и Service в одном namespace (default)
- Проверьте имя сервиса в Ingress совпадает с именем Service
- Проверьте, что поды приложения в статусе Running и Ready

### Проблема: 502 Bad Gateway
**Причины:**
1. Поды приложения не готовы
2. Неправильный порт в Service
3. Приложение не отвечает на health checks

**Решение:**
- Проверьте статус подов: `kubectl get pods -n default -l app=spring-students-app`
- Проверьте логи подов: `kubectl logs -n default -l app=spring-students-app`
- Проверьте, что приложение слушает на порту 8081

### Проблема: Connection refused
**Причины:**
1. ingress-nginx не запущен
2. LoadBalancer IP не назначен
3. MetalLB не работает

**Решение:**
- Проверьте статус ingress-nginx: `kubectl get pods -n ingress-nginx`
- Проверьте LoadBalancer IP: `kubectl get svc -n ingress-nginx ingress-nginx-controller`
- Проверьте MetalLB: `kubectl get pods -n metallb-system`

