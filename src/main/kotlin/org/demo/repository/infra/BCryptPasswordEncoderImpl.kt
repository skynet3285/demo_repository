package org.demo.repository.infra

import org.demo.repository.common.PasswordEncoder
import org.mindrot.jbcrypt.BCrypt
import org.springframework.stereotype.Component

@Component
class BCryptPasswordEncoderImpl : PasswordEncoder {
    /**
     * 비밀번호를 암호화합니다.
     * BCrypt.gensalt()는 기본적으로 log_rounds(복잡도) 10을 사용합니다.
     * 숫자가 높을수록 보안은 강력해지지만 생성 속도가 느려집니다. (10~12 권장)
     */
    override fun encode(raw: String): String = BCrypt.hashpw(raw, BCrypt.gensalt())

    /**
     * 입력된 비밀번호(raw)와 암호화된 비밀번호(encodedTarget)가 일치하는지 확인합니다.
     * BCrypt는 해시 값 내부에 Salt가 포함되어 있어, 별도의 Salt 관리가 필요 없습니다.
     */
    override fun matches(
        raw: String,
        encodedTarget: String,
    ): Boolean =
        try {
            BCrypt.checkpw(raw, encodedTarget)
        } catch (_: IllegalArgumentException) {
            false
        }
}
