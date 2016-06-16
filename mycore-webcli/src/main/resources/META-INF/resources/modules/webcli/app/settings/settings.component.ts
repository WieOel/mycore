import {Component, OnInit} from '@angular/core';
import {CommunicationService} from '../service/communication.service';
import {RESTService} from '../service/rest.service';
import {Settings} from './settings';

@Component({
  selector: 'web-cli-settings',
  templateUrl: 'app/settings/settings.html'
})
export class WebCliSettingsComponent {
  settings: Settings;

  constructor(private _communicationService: CommunicationService,
              private _restService: RESTService){}

  ngOnInit() {
    this.settings = this.getSettingsFromCookie(50, true, false);
    this._communicationService.setSettings(this.settings);
    this._restService.setContinueIfOneFails(this.settings.continueIfOneFails);
  }

  onHistoryChange() {
    if (localStorage.getItem("historySize") != this.settings.historySize + "") {
      localStorage.setItem("historySize", this.settings.historySize + "");
    }
  }

  onAutoScrollChange(event) {
    if (localStorage.getItem("autoScroll") != event.srcElement.checked + "") {
      localStorage.setItem("autoScroll", event.srcElement.checked);
    }
  }

  onContinueIfOneFailsChange(event) {
    if (localStorage.getItem("continueIfOneFails") != event.srcElement.checked + "") {
      localStorage.setItem("continueIfOneFails", event.srcElement.checked);
    }
    this._restService.setContinueIfOneFails(event.srcElement.checked);
  }

  private getSettingsFromCookie(defaultHSize: number, defaultAutoScroll: boolean, defaultContinueIfOneFails: boolean) {
    var storageHSize = localStorage.getItem("historySize");
    if (storageHSize != undefined && storageHSize != ""){
      defaultHSize = parseInt(storageHSize);
    }
    else {
      localStorage.setItem("historySize", defaultHSize + "");
    }
    var storageAutoScroll = localStorage.getItem("autoScroll");
    if (storageAutoScroll != undefined && storageAutoScroll != ""){
      defaultAutoScroll = (storageAutoScroll == "true");
    }
    else {
      localStorage.setItem("autoScroll", defaultAutoScroll +  "");
    }

    var storageContinueIfOneFails = localStorage.getItem("continueIfOneFails");
    if (storageContinueIfOneFails != undefined && storageContinueIfOneFails != ""){
      defaultContinueIfOneFails = (storageContinueIfOneFails == "true");
    }
    else {
      localStorage.setItem("defaultContinueIfOneFails", defaultContinueIfOneFails + "")
    }
    return new Settings(defaultHSize, defaultAutoScroll, defaultContinueIfOneFails);
  }
}
