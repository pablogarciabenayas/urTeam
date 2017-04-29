import { Component, AfterViewChecked } from '@angular/core';
import { Router, ActivatedRoute } from '@angular/router';

import { CommunityService } from './communities.service';
import { Community } from './community.model';

import { News } from "app/news/news.model";
import { LoginService } from "app/login/login.service";
import { User } from "app/user/user.model";
import { UserService } from "../user/user.service";
import {PublicComponent} from '../public.component';


@Component({
  templateUrl: './communities-detail.component.html',
  styleUrls: ['./communities-detail.component.css']
})
export class CommunityDetailComponent {
  groupName = "Comunidad 0";
  groupInfo = "AAA"
  groupCity = "toledo"
  groupSport = "Running"
  groupUsers: User[];
  imgUrl = "https://localhost:8443/image/event-avatar/aleatorio/default-mainphoto";
  community: Community;
  info: string;

  newsTitle: string;
  newsText: string;

  myService: CommunityService;

  login: boolean;

  groupId: number;

  groupNumberOfFollowers: number;

  user: User;
  ownerId: User;
  isOwner: boolean;

  groupImage: any;



  communityID: number;

  constructor(private router: Router, private userService: UserService, activatedRoute: ActivatedRoute, private service: CommunityService, private sessionService: LoginService,private pubComponent: PublicComponent) {
    this.login = false;
    let id = activatedRoute.snapshot.params['id'];
    this.communityID = id;
    this.groupId = id;
    this.getCommunity();
    this.groupUsers = [];
    this.community = { name: this.groupName, info: this.groupInfo, city: this.groupCity, main_photo: this.imgUrl, sport: this.groupSport, communityUsers: this.groupUsers };
    this.ownerId = { username: "", surname: "", nickname: "", email: "", country: "" };

  }

  getCommunity() {
    this.service.getGroup(this.communityID).subscribe(
      community => {
        this.community = community;
        this.groupUsers = community.communityUsers;
        this.user = this.sessionService.getUser();
        this.ownerId = community.owner_id;
        this.isOwner = (this.ownerId.nickname === this.user.nickname);

      },
      error => console.error(error),
      () => this.checkFollow()
    );
  }

  getUser() {
    this.userService.getUser(this.user.nickname).subscribe(
      user => { this.user = user; }
    )
  }

  editInfo() {
    this.community.info = this.info;
    this.service.updateGroup(this.communityID, this.community).subscribe(
      community => {
        console.log(community);
        this.pubComponent.msgs.push({severity:'success', summary:'Comunidad actualizada', detail:'Información actualizada satisfactoriamente'});
      },
      error => {
        console.error(error);
        this.pubComponent.msgs.push({severity:'error', summary:'Error', detail:'Se ha producido un fallo durante la actualización de la comunidad'});
      }
    );
  }

  addNews() {
    let updateNews: News;
    updateNews = { title: this.newsTitle, text: this.newsText }
    this.service.addNews(this.communityID, updateNews).subscribe(
      response => {
        this.groupUsers = response.communityUsers;
        this.getCommunity();
        this.getUser();
        this.pubComponent.msgs.push({severity:'success', summary:'Noticia creada', detail:'Nueva noticia creada satisfactoriamente'});
      },
      error => {
        console.error(error);
        this.pubComponent.msgs.push({severity:'error', summary:'Error', detail:'Se ha producido un fallo durante la creación de la noticia'});
      }
    );
  }

  followGroup() {
    this.service.followGroup(this.communityID).subscribe(
      response => {
        this.groupUsers = response.communityUsers;
        this.getCommunity();
        this.getUser();
        this.pubComponent.msgs.push({severity:'success', summary:'Acción realizada'});
      },
      error => {
       console.log(error);
       this.pubComponent.msgs.push({severity:'error', summary:'Error', detail:'Se ha producido un error al realizar acción'});
      }
    )
    // if (!this.checkFollowers()) {
    //   this.service.followGroup(this.communityID).subscribe(
    //     community => console.log(community),
    //     error => console.error(error),
    //   );
    // } else {
    //   this.unfollowGroup();
    // }
    // this.refresh();
  }

  unfollowGroup() {
    this.service.unfollowGroup(this.communityID).subscribe(
      response => {
        this.groupUsers = response.communityUsers;
        this.getCommunity();
        this.getUser();
        this.pubComponent.msgs.push({severity:'success', summary:'Acción realizada'});
      },
      error => {
        console.log(error);
        this.pubComponent.msgs.push({severity:'error', summary:'Error', detail:'Se ha producido un error al realizar acción'});
      }
    )
  }

  checkFollow() {
    if (this.sessionService.getisLogged()) {
      let cosa: boolean = (this.community.communityUsers.find(
        user1 => user1.nickname == this.user.nickname) != undefined);
      return cosa;
    }
  }

  uploadImage() {

    let formData = new FormData();
    formData.append('file', this.groupImage, this.groupImage.name);
    this.service.addImage(this.communityID, formData).subscribe(
      response => {
        this.groupUsers = response.communityUsers;
        this.getCommunity();
        this.getUser();
      },
      error => console.log(error),
    )
  }

  selectFile($event) {
    this.groupImage = $event.target.files[0];
    console.log("Selected file: " + this.groupImage.name + " type:" + this.groupImage.type + " size:" + this.groupImage.size);
  }

}


  // this.groupNumberOfFollowers = this.community.communityUsers.length;
  // if (this.sessionService.getisLogged) {
  //   this.login = true;
  //   for (let follower of this.community.communityUsers) {
  //     if (follower) {
  //       //console.log(follower.nickname === this.sessionService.getUser().nickname);
  //       if (follower.nickname === this.sessionService.getUser().nickname) {
  //         this.isFollowing = "DEJAR DE SEGUIR";
  //         return true;
  //       }
  //     }
  //   }
  // }


