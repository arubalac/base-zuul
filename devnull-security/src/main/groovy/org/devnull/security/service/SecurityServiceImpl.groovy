package org.devnull.security.service

import org.devnull.security.config.UserLookupStrategy
import org.devnull.security.dao.RoleDao
import org.devnull.security.dao.UserDao

import org.devnull.security.model.User
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.security.core.Authentication
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.core.context.SecurityContextHolder
import org.devnull.security.model.Role

@Service("securityService")
@Transactional
class SecurityServiceImpl implements SecurityService {

    final def log = LoggerFactory.getLogger(this.class)

    @Autowired
    UserDao userDao

    @Autowired
    RoleDao roleDao

    @Autowired
    UserLookupStrategy userLookupStrategy

    User getCurrentUser() {
        return userLookupStrategy.lookupCurrentUser()
    }

    @Transactional(readOnly = true)
    User findUserByOpenId(String openId) {
        return userDao.findByOpenId(openId)
    }

    User createNewUser(User user, List<String> roles) {
        roles.each { role ->
            log.debug("Adding user to role: {}", role)
            user.roles << roleDao.findByName(role)
        }
        userDao.save(user)
    }

    User updateCurrentUser(Boolean reAuthenticate) {
        def user = currentUser
        log.info("Saving user: {}", user)
        userDao.save(user)
        if (reAuthenticate) {
            userLookupStrategy.reAuthenticateCurrentUser()
        }
        return currentUser
    }

    Role findRoleByName(String name) {
        return roleDao.findByName(name)
    }


}
