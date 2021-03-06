package com.myapp.controller

import com.jarst.controller.FeedController
import com.jarst.domain.Micropost
import com.jarst.domain.User
import com.jarst.repository.MicropostRepository
import com.jarst.repository.UserRepository
import com.jarst.service.MicropostService
import com.jarst.service.MicropostServiceImpl
import com.jarst.service.SecurityContextService
import org.springframework.beans.factory.annotation.Autowired

import static org.hamcrest.Matchers.hasSize
import static org.hamcrest.Matchers.is
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

class FeedControllerTest extends BaseControllerTest {

    @Autowired
    MicropostRepository micropostRepository;

    @Autowired
    UserRepository userRepository;

    SecurityContextService securityContextService = Mock(SecurityContextService);

    def "can show feed"() {
        given:
        User user = userRepository.save(new User(username: "test1@test.com", password: "secret", name: "test"))
        micropostRepository.save(new Micropost(user: user, content: "content1"))
        securityContextService.currentUser() >> user

        when:
        def response = perform(get("/api/feed"))

        then:
        response
//                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(jsonPath('$', hasSize(1)))
                .andExpect(jsonPath('$[0].content', is("content1")))
                .andExpect(jsonPath('$[0].isMyPost', is(true)))
                .andExpect(jsonPath('$[0].createdAt').exists())
                .andExpect(jsonPath('$[0].user.email', is("test1@test.com")))
    }

    @Override
    def controllers() {
        MicropostService micropostService = new MicropostServiceImpl(micropostRepository, securityContextService)
        return new FeedController(micropostService)
    }
}
