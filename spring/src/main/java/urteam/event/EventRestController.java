package urteam.event;

import java.io.IOException;
import java.text.ParseException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.annotation.JsonView;

import urteam.user.User;
import urteam.user.UserComponent;
import urteam.user.UserRepository;
import urteam.user.UserService;

@RestController
@RequestMapping("/api/events")
public class EventRestController {

	interface CompleteEvent extends Event.MinimalEvent, Event.BasicEvent,Event.MembersEvent,User.MinimalUser {
	}

	interface FollowersEvent extends Event.MembersEvent, User.MinimalUser {
	}
	// interface FollowersEvent extends Event.MembersEvent{}

	@Autowired
	private EventService eventService;
	
	@Autowired
	private UserService userService;

	@Autowired
	private UserComponent userComponent;

	@Autowired
	private UserRepository userRepo;

	@JsonView(CompleteEvent.class)
	@RequestMapping(value = "/", method = RequestMethod.GET)
	public ResponseEntity<Page<Event>> getEvents(Pageable pageable) {
		Page<Event> events = eventService.findAll(pageable);
		if (events != null) {
			return new ResponseEntity<Page<Event>>(events, HttpStatus.OK);
		} else {
			return new ResponseEntity<Page<Event>>(HttpStatus.NOT_FOUND);
		}
	}
	
	@JsonView(CompleteEvent.class)
	@RequestMapping(value = "/sport/{sport}", method = RequestMethod.GET)
	public ResponseEntity<Page<Event>> getEventsBySport(Pageable pageable, @PathVariable String sport) {
		Page<Event> events = eventService.findBySport(sport, pageable);
		if (events != null) {
			return new ResponseEntity<Page<Event>>(events, HttpStatus.OK);
		} else {
			return new ResponseEntity<Page<Event>>(HttpStatus.NOT_FOUND);
		}
	}

	@JsonView(CompleteEvent.class)
	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	public ResponseEntity<Event> getEvent(@PathVariable long id) {
		Event event = eventService.findOne(id);
		if (event != null) {
			return new ResponseEntity<Event>(event, HttpStatus.OK);
		} else {
			return new ResponseEntity<Event>(HttpStatus.NOT_FOUND);
		}
	}

	@JsonView(FollowersEvent.class)
	@RequestMapping(value = "/{id}/members", method = RequestMethod.GET)
	public ResponseEntity<Event> eventMembers(@PathVariable long id) {
		Event event = eventService.findOne(id);
		if (event != null) {
			return new ResponseEntity<Event>(event, HttpStatus.OK);
		} else {
			return new ResponseEntity<Event>(HttpStatus.NOT_FOUND);
		}
	}

	@JsonView(FollowersEvent.class)
	@RequestMapping(value = "/{id}/members", method = RequestMethod.PUT)
	public ResponseEntity<Event> followEvent(@PathVariable long id) {
		Event event = eventService.findOne(id);
		User userLogged = userService.findOne(userComponent.getLoggedUser().getId());
		if (event != null && userLogged != null) {
			eventService.follow(userLogged, event);
			eventService.save(event);
			return new ResponseEntity<Event>(event, HttpStatus.OK);
		} else {
			return new ResponseEntity<Event>(HttpStatus.NOT_FOUND);
		}
	}

	@JsonView(CompleteEvent.class)
	@RequestMapping(value = "/", method = RequestMethod.POST)
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity<Event> createEvent(@RequestBody Event event) {
		User userLogged = userService.findOne(userComponent.getLoggedUser().getId());
		if (userLogged != null) {
			eventService.save(userLogged, event);
			return new ResponseEntity<Event>(event, HttpStatus.OK);
		} else {
			return new ResponseEntity<Event>(HttpStatus.BAD_REQUEST);
		}

	}
	
	@JsonView(CompleteEvent.class)
	@RequestMapping(value = "/{id}/avatar", method = RequestMethod.PUT)
	public ResponseEntity<Event> setImage(@PathVariable long id, @RequestBody MultipartFile file)
			throws ParseException {
		
		Event event = null;
		if (file != null){
		event = eventService.findOne(id);
		eventService.setImage(event, file);
		}
		if (event != null) {
			return new ResponseEntity<Event>(event, HttpStatus.OK);
		} else {
			return new ResponseEntity<Event>(HttpStatus.UNAUTHORIZED);
		}
	}
	

	@JsonView(CompleteEvent.class)
	@RequestMapping(value = "/{id}", method = RequestMethod.PUT)
	public ResponseEntity<Event> updateEvent(@PathVariable long id, @RequestBody Event updatedEvent) {
		Event event = eventService.findOne(id);
		User ownerEvent = userService.findOne(event.getOwner_id().getId());
		User userLogged = userService.findOne(userComponent.getLoggedUser().getId());
		if (userLogged.getId() == ownerEvent.getId()) {
			if (event != null && updatedEvent != null) {
				updatedEvent.setId(id);
				eventService.save(updatedEvent);
				return new ResponseEntity<>(updatedEvent, HttpStatus.ACCEPTED);
				// return new ResponseEntity<Event>(updatedEvent,
				// HttpStatus.OK);
			} else {
				return new ResponseEntity<Event>(HttpStatus.NOT_FOUND);
			}
		} else {
			return new ResponseEntity<Event>(HttpStatus.UNAUTHORIZED);
		}

	}

	@JsonView(CompleteEvent.class)
	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
	public ResponseEntity<Event> deleteEvent(@PathVariable long id) {
		eventService.delete(id);
		return new ResponseEntity<>(null, HttpStatus.NO_CONTENT);
	}
	
	
	@RequestMapping(value = "/{id}/avatar", method = RequestMethod.GET, produces = MediaType.IMAGE_JPEG_VALUE)
	public ResponseEntity<byte[]> get(@PathVariable long id) {
		byte[] eventAvatar;
		try {
			eventAvatar = eventService.getEventAvatar(id);
			if (eventAvatar != null) {
				final HttpHeaders headers = new HttpHeaders();
				headers.setContentType(MediaType.IMAGE_JPEG);
				return new ResponseEntity<>(eventAvatar, HttpStatus.OK);
			} else {
				return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
			}
		} catch (IOException e) {
			e.printStackTrace();
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
	}
	
	
	
}
