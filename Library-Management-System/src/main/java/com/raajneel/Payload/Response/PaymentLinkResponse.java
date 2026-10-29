package com.raajneel.Payload.Response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "Response containing generated external payment link details")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentLinkResponse {

    @Schema(description = "Unique identifier of generated payment link resource", example = "plink_LF78s0vLkdPa", requiredMode = Schema.RequiredMode.REQUIRED)
    private String payment_link_id;

    @Schema(description = "Hosted gateway checkout URL where patron completes card/UPI payment", example = "https://rzp.io/i/LF78s0vLkdPa", requiredMode = Schema.RequiredMode.REQUIRED)
    private String payment_link_url;
}
