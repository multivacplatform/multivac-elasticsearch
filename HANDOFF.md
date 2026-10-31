# Deployment handoff

This deployment uses checked-out source without Git metadata. Updates are made and tested in the operator repository, then deployed as reviewed files. Automatic Git pulls must stay disabled on the VM.

Personal provider credentials are not supplied. Optional collectors require fresh institutional provider credentials through their service environment. Service passwords, where needed, belong in protected runtime configuration outside Git. Never restore credentials from old Git history.
