package app.service

import app.service.mock.MockData
import com.parcel.app.enums.Status
import com.parcel.app.repo.OrderRepository
import com.parcel.app.service.OrderService
import com.parcel.app.util.mapper.OrderMapper
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContext
import org.springframework.security.core.context.SecurityContextHolder
import spock.lang.Specification

class OrderServiceTest extends Specification {

    private OrderService orderService;
    private OrderRepository orderRepository
    private OrderMapper orderMapper


    void setup() {
        orderRepository = Mock()
        orderMapper = Mock()
        orderService = new OrderService(orderRepository, orderMapper)
        setSecurityContext()
    }

    def setSecurityContext() {
        def authentication = Mock(Authentication)
        authentication.getPrincipal() >> MockData.userDetailsImp()
        def securityContext = Mock(SecurityContext)
        securityContext.getAuthentication() >> authentication
        SecurityContextHolder.setContext(securityContext)
    }

    def "CreateOrder"() {
        given:
        def req = MockData.createOrderReq()
        def entity = MockData.orderEntity()

        when:
        def resp = orderService.createOrder(req)

        then:
        1 * orderMapper.reqToEntity(req) >> entity
        1 * orderRepository.save(entity) >> entity
        1 * orderMapper.entityToResp(entity) >> MockData.createOrderResp()
        resp == MockData.createOrderResp()
    }

    def "AssignOrderToCourier"() {
        given:
        def orderId = MockData.orderId
        def courierId = MockData.courierId
        def updatedEntity = MockData.orderEntity()
        updatedEntity.setStatus(Status.ASSIGNED.name())
        updatedEntity.setCourierId(courierId)

        when:
        orderService.assignOrderToCourier(orderId, courierId)

        then:
        1 * orderRepository.findById(orderId) >> Optional.of(MockData.orderEntity())
        1 * orderRepository.save(updatedEntity)
    }
}
