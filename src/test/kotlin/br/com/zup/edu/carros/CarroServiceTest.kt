package br.com.zup.edu.carros

import br.com.zup.edu.CarroRequest
import br.com.zup.edu.CarrosGrpcServiceGrpc
import io.grpc.ManagedChannel
import io.grpc.Status
import io.grpc.StatusRuntimeException
import io.micronaut.context.annotation.Factory
import io.micronaut.grpc.annotation.GrpcChannel
import io.micronaut.grpc.server.GrpcServerChannel
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import javax.inject.Singleton

@MicronautTest(transactional = false)
internal class CarroServiceTest(
    val grpcClient: CarrosGrpcServiceGrpc.CarrosGrpcServiceBlockingStub,
    val repository: CarroRepository
){

    @Test
    internal fun `deve adicionar um novo carro`() {
        repository.deleteAll()

        val response = grpcClient.adicionar(CarroRequest.newBuilder()
            .setModelo("Gol")
            .setPlaca("HQP-2923").build())

        with(response){
            assertNotNull(id)
            assertTrue(repository.existsById(id))
        }
    }

    @Test
    internal fun `nao deve cadastrar um carro existente`() {
        repository.deleteAll()
        val existente = repository.save(Carro("Vectra", "YKH-3215"))

        val error = assertThrows<StatusRuntimeException>{
            grpcClient.adicionar(CarroRequest.newBuilder()
                .setModelo(existente.modelo)
                .setPlaca(existente.placa).build())
        }

        with(error){
            assertEquals(Status.ALREADY_EXISTS.code, status.code)
            assertEquals("Placa ja cadastrada", status.description)
        }
    }

    @Test
    internal fun `nao cadastrar carro com entrada invalida`() {
        repository.deleteAll()
        val error = assertThrows<StatusRuntimeException>{
            grpcClient.adicionar(CarroRequest.newBuilder().build())
        }

        with(error){
            assertEquals(Status.INVALID_ARGUMENT.code, status.code)
            assertEquals("Dados de entrada invalidos", status.description)
        }
    }

    @Factory
    class client{
        @Singleton
        fun blockingStub(@GrpcChannel(GrpcServerChannel.NAME) channel: ManagedChannel): CarrosGrpcServiceGrpc.CarrosGrpcServiceBlockingStub{
            return CarrosGrpcServiceGrpc.newBlockingStub(channel)
        }
    }
}