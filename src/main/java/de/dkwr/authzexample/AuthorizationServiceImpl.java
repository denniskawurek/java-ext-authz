package de.dkwr.authzexample;

import com.google.rpc.Code;
import com.google.rpc.Status;
import io.envoyproxy.envoy.service.auth.v3.AuthorizationGrpc;
import io.envoyproxy.envoy.service.auth.v3.CheckResponse;

import java.util.Map;
import java.util.Objects;

/**
 * Implementation of the ext-authz service for Envoy.
 * @see <a href="https://github.com/envoyproxy/envoy/blob/main/api/envoy/service/auth/v3/external_auth.proto">...</a>
 */
public class AuthorizationServiceImpl extends AuthorizationGrpc.AuthorizationImplBase {
    @Override
    public void check(io.envoyproxy.envoy.service.auth.v3.CheckRequest request,
                      io.grpc.stub.StreamObserver<io.envoyproxy.envoy.service.auth.v3.CheckResponse> done) {
        Map<String, String> headers = request.getAttributes().getRequest().getHttp().getHeadersMap();

        if (!Objects.equals(headers.get("x-ext-authz"), "allow")) {
            done.onNext(
                    CheckResponse
                            .newBuilder()
                            .setStatus(Status.newBuilder().setCode(Code.UNAUTHENTICATED_VALUE).build())
                            .setDeniedResponse(CheckResponse.getDefaultInstance().getDeniedResponse())
                            .build());

            done.onCompleted();
            return;
        }

        done.onNext(CheckResponse
                .newBuilder()
                .setStatus(Status.newBuilder().setCode(Code.OK_VALUE).build())
                .setOkResponse(CheckResponse.getDefaultInstance().getOkResponse())
                .build());
        done.onCompleted();
    }
}
