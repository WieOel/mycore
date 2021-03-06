System.register(['@angular/core', '../settings/settings', '../service/rest.service', '../service/communication.service'], function(exports_1, context_1) {
    "use strict";
    var __moduleName = context_1 && context_1.id;
    var __decorate = (this && this.__decorate) || function (decorators, target, key, desc) {
        var c = arguments.length, r = c < 3 ? target : desc === null ? desc = Object.getOwnPropertyDescriptor(target, key) : desc, d;
        if (typeof Reflect === "object" && typeof Reflect.decorate === "function") r = Reflect.decorate(decorators, target, key, desc);
        else for (var i = decorators.length - 1; i >= 0; i--) if (d = decorators[i]) r = (c < 3 ? d(r) : c > 3 ? d(target, key, r) : d(target, key)) || r;
        return c > 3 && r && Object.defineProperty(target, key, r), r;
    };
    var __metadata = (this && this.__metadata) || function (k, v) {
        if (typeof Reflect === "object" && typeof Reflect.metadata === "function") return Reflect.metadata(k, v);
    };
    var core_1, settings_1, rest_service_1, communication_service_1;
    var WebCliLogComponent;
    return {
        setters:[
            function (core_1_1) {
                core_1 = core_1_1;
            },
            function (settings_1_1) {
                settings_1 = settings_1_1;
            },
            function (rest_service_1_1) {
                rest_service_1 = rest_service_1_1;
            },
            function (communication_service_1_1) {
                communication_service_1 = communication_service_1_1;
            }],
        execute: function() {
            let WebCliLogComponent = class WebCliLogComponent {
                constructor(_restService, _comunicationService) {
                    this._restService = _restService;
                    this._comunicationService = _comunicationService;
                    this.settings = new settings_1.Settings(500, 10, true, false);
                    this._comunicationService.settings.subscribe(settings => {
                        this.settings = settings;
                    });
                    this._restService.currentLog.subscribe(log => {
                        if (log != undefined) {
                            if (document.getElementsByClassName('web-cli-log')[0].childNodes.length + 1 > this.settings.historySize) {
                                document.getElementsByClassName('web-cli-log')[0].removeChild(document.getElementsByClassName('web-cli-log')[0].childNodes[0]);
                            }
                            var node = document.createElement("pre");
                            var text = document.createTextNode(log.logLevel + ": " + log.message);
                            node.appendChild(text);
                            document.getElementsByClassName('web-cli-log')[0].appendChild(node);
                            if (log.exception != undefined) {
                                var nodeEx = document.createElement("pre");
                                var textEx = document.createTextNode(log.exception);
                                nodeEx.appendChild(textEx);
                                document.getElementsByClassName('web-cli-log')[0].appendChild(nodeEx);
                            }
                        }
                    });
                }
                clearLog() {
                    document.getElementsByClassName('web-cli-log')[0].innerHTML = "";
                }
                ngAfterViewChecked() {
                    this.scrollLog();
                }
                scrollLog() {
                    if (this.settings.autoscroll) {
                        var elem = document.getElementsByClassName('web-cli-log');
                        elem[0].scrollTop = elem[0].scrollHeight;
                    }
                }
            };
            WebCliLogComponent = __decorate([
                core_1.Component({
                    selector: '[web-cli-log]',
                    template: `
    <div class="col-xs-12 web-cli-log">
    </div>
  `
                }), 
                __metadata('design:paramtypes', [rest_service_1.RESTService, communication_service_1.CommunicationService])
            ], WebCliLogComponent);
            exports_1("WebCliLogComponent", WebCliLogComponent);
        }
    }
});
