package com.myapp.repository

import com.jarst.domain.Micropost
import com.jarst.domain.Relationship
import com.jarst.domain.User
import com.jarst.dto.PageParams
import com.jarst.dto.PostDTO
import com.jarst.repository.MicropostRepository
import com.jarst.repository.RelationshipRepository
import com.jarst.repository.UserRepository
import org.springframework.beans.factory.annotation.Autowired

class MicropostRepositoryTest extends BaseRepositoryTest {

    @Autowired
    MicropostRepository micropostRepository

    @Autowired
    UserRepository userRepository

    @Autowired
    RelationshipRepository relationshipRepository

    def "can find feed"() {
        given:
        User follower = userRepository.save(new User(username: "akira@test.com", password: "secret", name: "akira"))
        User followed = userRepository.save(new User(username: "test1@test.com", password: "secret", name: "test1"))
        User another = userRepository.save(new User(username: "test2@test.com", password: "secret", name: "test2"))
        relationshipRepository.save(new Relationship(follower: follower, followed: followed))
        [follower, followed, another].each { u ->
            micropostRepository.save(new Micropost(content: "test1", user: u))
            micropostRepository.save(new Micropost(content: "test2", user: u))
        }

        when:
        List<PostDTO> result = micropostRepository.findAsFeed(follower, new PageParams())

        then:
        result.size() == 4
        result.first().user.id == followed.id
        result.last().user.id == follower.id
    }

    def "can find feed by since_id or max_id"() {
        given:
        User user = userRepository.save(new User(username: "akira@test.com", password: "secret", name: "akira"))
        Micropost post1 = micropostRepository.save(new Micropost(content: "test1", user: user))
        Micropost post2 = micropostRepository.save(new Micropost(content: "test2", user: user))
        Micropost post3 = micropostRepository.save(new Micropost(content: "test3", user: user))

        when:
        List<PostDTO> result = micropostRepository.findAsFeed(user, new PageParams(sinceId: post2.id))

        then:
        result.size() == 1
        result.first().id == post3.id

        when:
        result = micropostRepository.findAsFeed(user, new PageParams(maxId: post2.id))

        then:
        result.size() == 1
        result.first().id == post1.id
    }

    def "can find posts by user"() {
        given:
        User user = userRepository.save(new User(username: "akira@test.com", password: "secret", name: "akira"))
        Micropost post1 = micropostRepository.save(new Micropost(content: "test1", user: user))
        Micropost post2 = micropostRepository.save(new Micropost(content: "test2", user: user))
        Micropost post3 = micropostRepository.save(new Micropost(content: "test3", user: user))

        when:
        List<Micropost> result = micropostRepository.findByUser(user, new PageParams(sinceId: post2.id))

        then:
        result.size() == 1
        result.first() == post3

        when:
        result = micropostRepository.findByUser(user, new PageParams(maxId: post2.id))

        then:
        result.size() == 1
        result.first() == post1
    }

}
