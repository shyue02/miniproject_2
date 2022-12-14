package site.metacoding.miniproject.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import site.metacoding.miniproject.domain.company.CompanyDao;
import site.metacoding.miniproject.domain.need_skill.NeedSkillDao;
import site.metacoding.miniproject.domain.notice.NoticeDao;
import site.metacoding.miniproject.domain.person.Person;
import site.metacoding.miniproject.domain.person.PersonDao;
import site.metacoding.miniproject.domain.person_skill.PersonSkillDao;
import site.metacoding.miniproject.domain.recommend.Recommend;
import site.metacoding.miniproject.domain.recommend.RecommendDao;
import site.metacoding.miniproject.domain.resume.Resume;
import site.metacoding.miniproject.domain.resume.ResumeDao;
import site.metacoding.miniproject.domain.submit_resume.SubmitResume;
import site.metacoding.miniproject.domain.submit_resume.SubmitResumeDao;
import site.metacoding.miniproject.domain.user.User;
import site.metacoding.miniproject.domain.user.UserDao;
import site.metacoding.miniproject.dto.SessionUserDto;
import site.metacoding.miniproject.dto.request.person.PersonJoinReqDto;
import site.metacoding.miniproject.dto.request.person.PersonMyPageUpdateReqDto;
import site.metacoding.miniproject.dto.request.resume.ResumeWriteReqDto;
import site.metacoding.miniproject.dto.response.notice.AppliersRespDto;
import site.metacoding.miniproject.dto.response.notice.CloseNoticeRespDto;
import site.metacoding.miniproject.dto.response.notice.FindNoticePerApplierRespDto;
import site.metacoding.miniproject.dto.response.notice.NoticeApplyRespDto;
import site.metacoding.miniproject.dto.response.notice.NoticeDetailRespDto;
import site.metacoding.miniproject.dto.response.person.InterestPersonRespDto;
import site.metacoding.miniproject.dto.response.person.PersonInfoRespDto;
import site.metacoding.miniproject.dto.response.person.PersonJoinRespDto;
import site.metacoding.miniproject.dto.response.person.PersonMyPageRespDto;
import site.metacoding.miniproject.dto.response.person.PersonMyPageUpdateRespDto;
import site.metacoding.miniproject.dto.response.person.PersonRecommendListRespDto;
import site.metacoding.miniproject.dto.response.recommend.RecommendDetailRespDto;
import site.metacoding.miniproject.dto.response.resume.ResumeDeleteRespDto;
import site.metacoding.miniproject.dto.response.resume.ResumeFormRespDto;
import site.metacoding.miniproject.dto.response.resume.ResumeWriteRespDto;
import site.metacoding.miniproject.handler.ApiException;

@RequiredArgsConstructor
@Service
public class PersonService {

	private final CompanyDao companyDao;
	private final PersonDao personDao;
	private final UserDao userDao;
	private final PersonSkillDao personSkillDao;
	private final ResumeDao resumeDao;
	private final RecommendDao recommendDao;
	private final SubmitResumeDao submitResumeDao;
	private final NoticeDao noticeDao;
	private final NeedSkillDao needSkillDao;

	@Transactional(rollbackFor = { RuntimeException.class })
	public PersonJoinRespDto ????????????(PersonJoinReqDto personJoinReqDto) {
		userDao.insert(personJoinReqDto.toUser());
		User userPS = userDao.findByUsername(personJoinReqDto.getUsername());
		personDao.insert(personJoinReqDto.toPerson(userPS.getUserId()));
		Integer personId = personDao.findToId(userPS.getUserId());
		List<String> personSkillList = personJoinReqDto.getPersonSkillList();

		for (int i = 0; i < personSkillList.size(); i++) {
			personSkillDao.insert(personId, personSkillList.get(i));
		}
		PersonJoinRespDto personJoinRespDto = new PersonJoinRespDto();
		personJoinRespDto.setPersonSkillList(personSkillDao.findByPersonId(personId));
		return personJoinRespDto;
	}

	@Transactional
	public ResumeFormRespDto ???????????????????????????(Integer userId) {
		Integer personId = personDao.findToId(userId);
		Person person = personDao.findById(personId);
		ResumeFormRespDto resumeFormDto = new ResumeFormRespDto(person, personSkillDao.findByPersonId(personId));
		return resumeFormDto;
	}

	@Transactional
	public List<Integer> ??????????????????????????????(List<String> skillList) {

		List<Integer> interesPersonIdList = new ArrayList<Integer>();

		List<Person> personList = personDao.findAll();

		for (int i = 0; i < personList.size(); i++) {
			int count = 0;
			int personId = personList.get(i).getPersonId();
			for (int j = 0; j < skillList.size(); j++) {
				if (personSkillDao.findBySkillAndPersonId(skillList.get(j), personId) == null) {
					continue;
				}
				count++;
			}
			if (count >= skillList.size()) {
				interesPersonIdList.add(personId);
			}
		}

		return interesPersonIdList;
	}

	@Transactional
	public List<Integer> ??????????????????????????????(String degree) {
		List<Integer> personIdList = personDao.findByDegree(degree);
		return personIdList;
	}

	@Transactional
	public List<Integer> ??????????????????????????????(Integer career) {
		List<Integer> personIdList = personDao.findByCareer(career);
		return personIdList;
	}

	@Transactional
	public List<InterestPersonRespDto> ????????????????????????(List<String> skillList) {
		List<Integer> interesPersonIdList = new ArrayList<Integer>();

		List<Person> personList = personDao.findAll();

		for (int i = 0; i < personList.size(); i++) {
			int count = 0;
			int personId = personList.get(i).getPersonId();
			for (int j = 0; j < skillList.size(); j++) {
				if (personSkillDao.findBySkillAndPersonId(skillList.get(j), personId) == null) {
					continue;
				}
				count++;
			}
			if (count >= skillList.size()) {
				interesPersonIdList.add(personId);
			}
		}

		List<InterestPersonRespDto> interestPersonDtoList = new ArrayList<InterestPersonRespDto>();
		int count = 0;

		for (int i = 0; i < interesPersonIdList.size(); i++) {
			count++;
			Person person = personDao.findById(interesPersonIdList.get(i));
			RecommendDetailRespDto CompanyDetailRecomDto = recommendDao.findAboutsubject(null, person.getUserId());
			InterestPersonRespDto interestPersonDto = new InterestPersonRespDto(person.getPersonId(), false,
					CompanyDetailRecomDto.getRecommendCount(), person.getPersonName(), person.getCareer(),
					person.getDegree(), person.getAddress(), personSkillDao.findByPersonId(interesPersonIdList.get(i)));

			interestPersonDtoList.add(interestPersonDto);
			if (count >= 20) {
				break;
			}
		}

		return interestPersonDtoList;
	}

	@Transactional
	public ResumeWriteRespDto ???????????????(ResumeWriteReqDto resumeWriteDto, Integer userId) {

		Resume resume = resumeWriteDto.toEntity();
		resumeDao.insert(resume);
		ResumeWriteRespDto resumeWriteRespDto = resumeDao
				.resumeWriteResult(resumeWriteDto.getPersonId());
		return resumeWriteRespDto;
	}

	@Transactional
	public Integer ????????????????????????(Integer userId) {
		return personDao.findToId(userId);
	}

	@Transactional
	public PersonInfoRespDto ??????????????????(Integer personId, SessionUserDto userPS) {
		PersonInfoRespDto personInfoRespDto = personDao.personInfo(personId);
		personInfoRespDto.setSkill(personSkillDao.findByPersonId(personId));
		if (userPS == null) {
			personInfoRespDto
					.insertRecommend(recommendDao.findAboutsubject(null, personInfoRespDto.getUserId()));
		} else {
			personInfoRespDto
					.insertRecommend(recommendDao.findAboutsubject(userPS.getUserId(), personInfoRespDto.getUserId()));
		}
		return personInfoRespDto;
	}

	@Transactional
	public List<PersonRecommendListRespDto> ??????????????????????????????(SessionUserDto userPS) {
		List<PersonRecommendListRespDto> personRecommendListDto = personDao.findToPersonRecommned();
		for (int i = 0; i < personRecommendListDto.size(); i++) {
			Integer personId = personRecommendListDto.get(i).getPersonId();
			personRecommendListDto.get(i).setSkill(personSkillDao.findByPersonId(personId));
		}

		if (userPS != null) {
			List<String> skillList = needSkillDao.findByUserId(userPS.getUserId());
			if (skillList != null) {
				for (int i = 0; i < personRecommendListDto.size(); i++) {
					int count = 0;
					List<String> personSkillList = personSkillDao
							.findByPersonId(personRecommendListDto.get(i).getPersonId());
					for (int j = 0; j < personSkillList.size(); j++) {
						for (int j2 = 0; j2 < skillList.size(); j2++) {
							if (personSkillList.get(j).equals(skillList.get(j2))) {
								count++;
							}
						}
					}
					if (skillList.size() * 0.7999999999 <= count) {
						personRecommendListDto.get(i).setMark(true);
					}
				}
			}
		}

		return personRecommendListDto;
	}

	// ????????? ??????????????? ?????? ??????
	@Transactional
	public PersonMyPageRespDto ????????????????????????????????????(Integer userId) {
		PersonMyPageRespDto personMyPageRespDtoPs = personDao.findToPersonMyPage(userId);
		personMyPageRespDtoPs.setSkill(personSkillDao.findByPersonId(personMyPageRespDtoPs.getPersonId()));
		return personMyPageRespDtoPs;

	}

	// ????????? ??????????????? ?????? ??????
	@Transactional
	public PersonMyPageUpdateRespDto ???????????????????????????(PersonMyPageUpdateReqDto PersonMyPageUpdateReqDto) {
		personDao.updateToPerson(PersonMyPageUpdateReqDto);
		userDao.updateToUser(PersonMyPageUpdateReqDto);
		PersonMyPageUpdateRespDto personMyPageUpdateRespDto = personDao
				.personMyPageUpdateResult(PersonMyPageUpdateReqDto.getUserId());
		personMyPageUpdateRespDto.setSkill(personSkillDao.findByPersonId(personMyPageUpdateRespDto.getPersonId()));
		return personDao.personMyPageUpdateResult(PersonMyPageUpdateReqDto.getUserId());
	}

	@Transactional
	public List<Resume> ???????????????????????????(Integer userId) {
		List<Resume> resumeList = resumeDao.findByPersonId(personDao.findToId(userId));
		return resumeList;
	}

	@Transactional
	public ResumeDeleteRespDto ?????????????????????(Integer resumeId, Integer userId) {
		Resume resume = resumeDao.findById(resumeId);
		Integer personId = personDao.findToId(userId);
		if (resume.getPersonId() != personId) {
			throw new ApiException("?????? ???????????? ????????? ??? ????????????.");
		}
		ResumeDeleteRespDto resumeDeleteRespDto = resumeDao.resumeDeleteResult(resumeId);
		resumeDao.deleteById(resumeId);
		return resumeDeleteRespDto;
	}

	@Transactional
	public List<PersonRecommendListRespDto> ???????????????????????????() {
		List<PersonRecommendListRespDto> PersonRecommendDtoList = personDao.findToPersonRecommned();
		for (int i = 0; i < PersonRecommendDtoList.size(); i++) {
			PersonRecommendDtoList.get(i)
					.setSkill(personSkillDao.findByPersonId(PersonRecommendDtoList.get(i).getPersonId()));
		}
		return PersonRecommendDtoList;
	}

	@Transactional
	public RecommendDetailRespDto ???????????????????????????(Integer userId, Integer subjectId) {
		return recommendDao.findAboutsubject(userId, subjectId);
	}

	@Transactional
	public void ?????????????????????(Integer userId, Integer subjectId) {
		Recommend recommend = new Recommend(null, userId, subjectId, null);
		recommendDao.insert(recommend);
	}

	@Transactional
	public void ?????????????????????(Integer recommendId) {
		recommendDao.delete(recommendId);
	}

	@Transactional
	public List<AppliersRespDto> ????????????????????????(Integer noticeId) {
		List<SubmitResume> submitResumedList = submitResumeDao.findByNoticeId(noticeId);
		List<AppliersRespDto> appliersDtoList = new ArrayList<>();
		for (int i = 0; i < submitResumedList.size(); i++) {
			Integer personId = resumeDao.findById(submitResumedList.get(i).getResumeId()).getPersonId();
			Person person = personDao.findById(personId);
			personDao.findById(personId);
			List<String> personSkillList = personSkillDao.findByPersonId(personId);
			appliersDtoList
					.add(new AppliersRespDto(submitResumedList.get(i).getResumeId(), personId, person.getPersonName(),
							person.getCareer(), personSkillList, submitResumedList.get(i).getCreatedAt()));
		}
		return appliersDtoList;
	}

	@Transactional
	public NoticeDetailRespDto ????????????????????????(int noticeId, SessionUserDto userPS) {
		NoticeDetailRespDto noticeDetailRespDto = new NoticeDetailRespDto(noticeDao.findById(noticeId));
		noticeDetailRespDto.setNeedSkillList(needSkillDao.findByNoticeId(noticeId));
		if (userPS != null) {
			noticeDetailRespDto.setResumeList(resumeDao.findByPersonId(personDao.findToId(userPS.getUserId())));
		}
		return noticeDetailRespDto;
	}

	@Transactional
	public FindNoticePerApplierRespDto ???????????????????????????(Integer noticeId) {
		FindNoticePerApplierRespDto findNoticePerApplierRespDto = new FindNoticePerApplierRespDto(
				noticeDao.findById(noticeId));
		List<SubmitResume> submitResumedList = submitResumeDao.findByNoticeId(noticeId);
		List<AppliersRespDto> appliersDtoList = new ArrayList<>();
		for (int i = 0; i < submitResumedList.size(); i++) {
			Integer personId = resumeDao.findById(submitResumedList.get(i).getResumeId()).getPersonId();
			Person person = personDao.findById(personId);
			personDao.findById(personId);
			List<String> personSkillList = personSkillDao.findByPersonId(personId);
			appliersDtoList
					.add(new AppliersRespDto(submitResumedList.get(i).getResumeId(), personId, person.getPersonName(),
							person.getCareer(), personSkillList, submitResumedList.get(i).getCreatedAt()));
		}
		findNoticePerApplierRespDto.setAppliersDtoList(appliersDtoList);
		return findNoticePerApplierRespDto;
	}

	@Transactional
	public CloseNoticeRespDto ??????????????????(Integer noticeId, Integer userId) {
		if (companyDao.findToCompanyMyPage(userId).getCompanyId() != noticeDao.findById(noticeId).getCompanyId()) {
			throw new ApiException("?????? ????????? ????????? ??? ????????????.");
		}
		noticeDao.closeNotice(noticeId, true);
		CloseNoticeRespDto closeNoticeRespDto = noticeDao.closeNoticeResult(noticeId);
		return closeNoticeRespDto;
	}

	@Transactional
	public List<NoticeApplyRespDto> ??????????????????(Integer userId) {
		List<NoticeApplyRespDto> noticeApplyDtoList = noticeDao.findNoticeApply(userId);
		for (int i = 0; i < noticeApplyDtoList.size(); i++) {
			noticeApplyDtoList.get(i)
					.setNeedSkillList(needSkillDao.findByNoticeId(noticeApplyDtoList.get(i).getNoticeId()));
		}
		return noticeApplyDtoList;
	}

	@Transactional
	public List<String> ?????????????????????????????????????????????????????????(Integer userId) {
		List<String> skillList = needSkillDao.findByUserId(userId);
		return skillList;
	}

	@Transactional
	public List<PersonRecommendListRespDto> ?????????(List<PersonRecommendListRespDto> personIdList, List<String> skillList) {

		for (int i = 0; i < personIdList.size(); i++) {
			int count = 0;
			List<String> personSkillList = personSkillDao.findByPersonId(personIdList.get(i).getPersonId());
			for (int j = 0; j < personSkillList.size(); j++) {
				for (int j2 = 0; j2 < skillList.size(); j2++) {
					if (personSkillList.get(j).equals(skillList.get(j2))) {
						count++;
					}
				}
			}
			if (skillList.size() * 0.7999999999 <= count) {
				personIdList.get(i).setMark(true);
			}
		}
		return personIdList;
	}

}
