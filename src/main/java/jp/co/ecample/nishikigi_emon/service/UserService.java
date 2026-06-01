package jp.co.ecample.nishikigi_emon.service;

import java.util.Optional;

import org.springframework.stereotype.Service;

import jp.co.ecample.nishikigi_emon.entity.User;
import jp.co.ecample.nishikigi_emon.repository.UserRepository;

@Service
public class UserService {
	private final UserRepository Urepository;

	public UserService(UserRepository Urepository) {
		this.Urepository = Urepository;
	}
	
	// ユーザーIDとパスワードを返す
		public Optional<User> login(int userid, String password) {
			return Urepository.findByUseridAndPassword(userid, password);
		}
}
