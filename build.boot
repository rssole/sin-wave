(set-env!
  :source-paths #{"src/clj" "src/cljs" "src/cljc"}
  :resource-paths #{"html"}

  :dependencies '[
                  [org.clojure/clojure "1.8.0"]
                  [org.clojure/clojurescript "1.9.293"]
                  [adzerk/boot-cljs "1.7.228-2"]            ;; cljs compliation
                  [pandeiro/boot-http "0.7.6"]              ;; for serving app
                  [adzerk/boot-reload "0.5.0"]              ;; reload of app via wsockets
                  [adzerk/boot-cljs-repl "0.3.3"]           ;; browser based REPL
                  [com.cemerick/piggieback "0.2.1"]         ;; needed by bREPL
                  [weasel "0.7.0"]                          ;; needed by bREPL
                  [org.clojure/tools.nrepl "0.2.12"]        ;; needed by bREPL
                  [org.clojars.magomimmo/domina "2.0.0-SNAPSHOT"]
                  [hiccups "0.3.0"]
                  [compojure "1.5.2"]                       ;; routing lib
                  [org.clojars.magomimmo/shoreleave-remote-ring "0.3.3"]
                  [org.clojars.magomimmo/shoreleave-remote "0.3.1"]
                  [javax.servlet/servlet-api "2.5"]     ;; for dev only
                  [org.clojars.magomimmo/valip "0.4.0-SNAPSHOT"]])


(task-options!
  pom {:project 'sin-wave
       :version "0.1.0"})

(require '[adzerk.boot-cljs :refer [cljs]]
         '[pandeiro.boot-http :refer [serve]]
         '[adzerk.boot-reload :refer [reload]]
         '[adzerk.boot-cljs-repl :refer [cljs-repl start-repl]])

(deftask dev
         "Launch Immediate Feedback Development Environment"
         []
         (comp
           (serve :handler 'sin-wave.remotes/app            ;; add ring handler
                  :resource-root "target"                   ;; add resource-path
                  :reload true)                             ;; reload server side ns
           (watch)
           (reload)
           (cljs-repl)                                      ;; before cljs task
           (cljs)
           (target :dir #{"target"})))