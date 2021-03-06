package com.myapp.controller

import com.jarst.controller.MicropostController
import com.jarst.domain.Micropost
import com.jarst.domain.User
import com.jarst.repository.MicropostRepository
import com.jarst.repository.UserRepository
import com.jarst.service.MicropostService
import com.jarst.service.MicropostServiceImpl
import com.jarst.service.SecurityContextService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType

import static groovy.json.JsonOutput.toJson
import static org.hamcrest.Matchers.is
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

class MicropostControllerTest extends BaseControllerTest {

    @Autowired
    MicropostRepository micropostRepository;

    @Autowired
    UserRepository userRepository;

    MicropostService micropostService;

    SecurityContextService securityContextService = Mock(SecurityContextService);

    @Override
    def controllers() {
        micropostService = new MicropostServiceImpl(micropostRepository, securityContextService)
        return new MicropostController(micropostRepository, micropostService, securityContextService)
    }

    def "can create a micropost"() {
        given:
        String content = "my content"
        User user = userRepository.save(new User(username: "test@test.com", password: "secret", name: "test"))
        securityContextService.currentUser() >> user

        when:
        def response = perform(post("/api/microposts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(content: content))
        )

        then:
        response.andExpect(status().isOk())
                .andExpect(jsonPath('$.content').exists())
                .andExpect(jsonPath('$.content', is(content)))
        micropostRepository.count() == 1
    }

    def "can delete a micropost"() {
        given:
        User user = userRepository.save(new User(username: "test@test.com", password: "secret", name: "test"))
        Micropost micropost = micropostRepository.save(new Micropost(user, "content"))
        securityContextService.currentUser() >> user

        when:
        def response = perform(delete("/api/microposts/${micropost.id}"))

        then:
        response.andExpect(status().isOk())
        micropostRepository.count() == 0
    }

}
