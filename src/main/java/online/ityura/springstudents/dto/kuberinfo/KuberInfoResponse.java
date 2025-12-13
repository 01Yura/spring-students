package online.ityura.springstudents.dto.kuberinfo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class KuberInfoResponse {
    private String podName;
    private String nodeName;
    private String podIP;
    private String nodeIP;
    private String osName;
    private String osVersion;
    private String osArch;
    private String hostname;
    private String timestamp;
    private String jvmUptime;
}

