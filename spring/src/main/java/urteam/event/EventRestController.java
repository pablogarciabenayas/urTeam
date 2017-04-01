package urteam.event;

import java.io.IOException;
import java.util.List;

import org.apache.catalina.servlet4preview.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.annotation.JsonView;

import urteam.event.EventService;
import urteam.user.User;
import urteam.user.UserComponent;
import urteam.user.UserRepository;

@RestController
@RequestMapping("/api/events")
public class EventRestController {
	
	interface CompleteEvent extends Event.BasicEvent, Event.MembersEvent{}
	
	@Autowired
	private EventService eventService;
	
	@Autowired
	private UserComponent userComponent;
	
	@Autowired
	private UserRepository userRepo;
	
	@Autowired
	private EventRepository eventRepo;
	

	@JsonView(CompleteEvent.class)
	@RequestMapping(value = "/", method = RequestMethod.GET)
	public List<Event> getEvents() {
		return eventService.findAll();
	}
	
	@JsonView(CompleteEvent.class)
	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	public ResponseEntity<Event> getEvent(@PathVariable long id){
		Event event = eventService.findOne(id);
		if(event != null){
			return new ResponseEntity<Event>(event, HttpStatus.OK);
		}else{
			return new ResponseEntity<Event>(HttpStatus.NOT_FOUND);
		}
	}
	
	@JsonView(CompleteEvent.class)
	@RequestMapping(value = "/{id}", method = RequestMethod.POST)
	public ResponseEntity<Event> followEvent(@PathVariable long id){
		Event event = eventService.findOne(id);
		User userLogged = userRepo.findOne(userComponent.getLoggedUser().getId());
		if(event != null && userLogged != null){
			eventService.follow(userLogged, event);
			eventService.save(event);
			return new ResponseEntity<Event>(event, HttpStatus.OK);
		}else{
			return new ResponseEntity<Event>(HttpStatus.NOT_FOUND);
		}			
	}
	
	@JsonView(CompleteEvent.class)
	@RequestMapping(value = "/", method = RequestMethod.POST)
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity<Event> createEvent(@RequestBody Event event, String start_date, String end_date, MultipartFile file){
		User userLogged = userRepo.findOne(userComponent.getLoggedUser().getId());
		if(userLogged != null){
			eventService.save(userLogged,event,file,start_date,end_date);
			return new ResponseEntity<Event>(event, HttpStatus.OK);
		}else{
			return new ResponseEntity<Event>(HttpStatus.BAD_REQUEST);
		}
		
	}
	
	@JsonView(CompleteEvent.class)
	@RequestMapping(value = "/{id}", method = RequestMethod.PUT)
	public ResponseEntity<Event> updateEvent(@PathVariable long id, @RequestBody Event updatedEvent){
		Event event = eventService.findOne(id);
		User ownerEvent = userRepo.findOne(event.getOwner_id().getId());
		User userLogged = userRepo.findOne(userComponent.getLoggedUser().getId());
		if (userLogged.getId() == ownerEvent.getId()) {
			if(event != null && updatedEvent != null){
				updatedEvent.setId(id);
				eventService.save(updatedEvent);
				return new ResponseEntity<>(updatedEvent, HttpStatus.ACCEPTED);
				//return new ResponseEntity<Event>(updatedEvent, HttpStatus.OK);
			}else{
				return new ResponseEntity<Event>(HttpStatus.NOT_FOUND);
			}
		}else{
			return new ResponseEntity<Event>(HttpStatus.UNAUTHORIZED);
		}
		
	}
	
	@JsonView(CompleteEvent.class)
	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
	public ResponseEntity<Event> deleteEvent(@PathVariable long id){
		eventService.delete(id);
		return new ResponseEntity<>(null, HttpStatus.NO_CONTENT);
	}
}