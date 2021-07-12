package br.com.zup.edu.carros

import br.com.zup.edu.CarroRequest
import br.com.zup.edu.CarroResponse
import br.com.zup.edu.CarrosGrpcServiceGrpc
import io.grpc.Status
import io.grpc.stub.StreamObserver
import javax.inject.Inject
import javax.inject.Singleton
import javax.validation.ConstraintViolationException

@Singleton
class CarroService(@Inject val repository: CarroRepository): CarrosGrpcServiceGrpc.CarrosGrpcServiceImplBase() {

    override fun adicionar(request: CarroRequest?, responseObserver: StreamObserver<CarroResponse>?) {

        if (repository.existsByPlaca(request!!.placa)){

            responseObserver?.onError(Status.ALREADY_EXISTS
                .withDescription("Placa ja cadastrada")
                .asRuntimeException())
            return
        }

        val carro = Carro(request.modelo, request.placa)

        try {
            repository.save(carro)
        }catch (e: ConstraintViolationException){
            responseObserver?.onError(Status.INVALID_ARGUMENT
                .withDescription("Dados de entrada invalidos")
                .asRuntimeException())
            return
        }
        responseObserver?.onNext(CarroResponse.newBuilder().setId(carro.id!!).build())
        responseObserver?.onCompleted()
    }
}